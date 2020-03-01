include "globals.mzn";

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    PARAMETERS    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% The different properties of each ratio constraint.
% These properties are instantiated in the "ratios" parameter variable.
% - Numerator:   the numerator of the provided ratio
% - Denominator: the denominator of the provided ratio
% - Priority:    whether the constraint is high-priority (1) or not (0)
enum RatioProperties = {Numerator, Denominator, Priority};

% The different properties of each vehicle.
% These properties are instantiated in the "vehicles" parameter variable.
% - Date:        whether the vehicle is from the previous day (0) or not (1)
% - PaintColour: which colour of paint the vehicle has
enum VehicleProperties = {Date, PaintColour};

% The different objectives within the situation.
% - HPRatioConstraints: the objective regarding the ratio constraints with Priority == 1.
% - LPRatioConstraints: the objective regarding the ratio constraints with Priority == 0.
% - PaintColourBatches: the objective regarding the number of paint changes.
enum Objectives = {HPRatioConstraints, LPRatioConstraints, PaintColourBatches};

% The upper limit on the batch size of paint colour (how many consequetive same-colour vehicles are allowed).
int: paintBatchLimit;

% Number of vehicles within the scenario.
int: numVehicles;

% The names of the available ratio constraints.
enum Constraints;

% The properties of each available ratio constraint.
array[Constraints,  RatioProperties] of int: ratios;

% The associated ratio constraints for each vehicle.
% Each cell corresponds to whether a vehicle is associated with a constraint (1) or not (0).
array[1..numVehicles, Constraints] of 0..1: associatedConstraints;

% The associated other properties for each vehicle, as described in "VehicleProperties".
% Note that the ordering of the vehicles of the original datafiles are preserved.
% Therefore, the schedule of the previous day (the vehicles with "Date" == 0) can be derived
% from the ordering within this matrix.
array[1..numVehicles, VehicleProperties] of int: vehicles;

% The multipliers of each available objective, which are described in "Objectives."
% In the original datafile, the first-ranked objective receives a multiplier of 10000,
% the second a multiplier of 100 and the third a multiplier of 1. If an objective is
% not included in the optimization objectives, its multiplier is 0.
array[Objectives] of int: objectiveMultipliers;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    DECISION VARIABLES    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% The vehicles in the order they will be processed. This is our main, key decision variable
% whose values will dictate the values of all the others. We will be using its name, "schedule", often.
array[1..numVehicles] of var 1..numVehicles: schedule;

% An array that describes the number of violations per constraint in our schedule.
array[Constraints] of var int: violations;

% Total number of violations of the high-priority constraints.
% The associated constraint is basically defining the decision variable, summing
% up all the different high-priority constraint violations counted in the schedule.
var int : highViolations :: is_defined_var;
constraint highViolations = sum(c in Constraints where ratios[c,Priority] = 1)(violations[c]) :: defines_var(highViolations);

% Total number of violations of the low-priority constraints.
% The associated constraint is basically defining the decision variable, summing
% up all the different low-priority constraint violations counted in the schedule.
var int : lowViolations :: is_defined_var;
constraint lowViolations = sum(c in Constraints where ratios[c,Priority] = 0)(violations[c]) :: defines_var(lowViolations);

% The variable represents how many times the paint has been changed in the schedule,
% and is defined by the constraint counting the number of different-colour pairs of vehicles
% in the schedule.
var int : paintChanges :: is_defined_var;
constraint paintChanges = count(
  [ vehicles[schedule[i], PaintColour] != vehicles[schedule[i+1],PaintColour]  | i in numPreviousDayVehicles+1..numVehicles-1], 
    true
) :: defines_var(paintChanges);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    SUPPORT VARIABLES    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Support, non-decision variables.
int : numPreviousDayVehicles = count(vehicles[..,Date],0);  % Number of vehicles from the previous day.
int : numOfColours = max(vehicles[..,PaintColour]); % Number of different colours.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    CONSTRAINTS    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Ensures that no vehicle ID is used twice within the schedule.
% Here, domain consistency rules out an option for every other cell in the “schedule” array 
% every time a cell is assigned a value. Helpful, and not significantly negatively impactful on the runtime.
constraint all_different(schedule) :: domain;

% If a vehicle belongs to the previous day, then its place in the schedule is the same as its place in the "vehicles" array.
% Both constraint-versions are kept to test which one is faster.
constraint forall (i in 1..numVehicles) ((vehicles[i,Date]=0 /\ schedule[i] != i) = false) :: domain;
%constraint forall (i in 1..numVehicles) (vehicles[i, Date] = 0 -> schedule[i] = i) :: domain;

% This constraint ensures there are no sequences of vehicles of the same paint color in the schedule
% that are longer than allowed by paintBatchLimit. However, as a preprocessing measure, the constraint
% is only applied if there is at least one colour, for which the number of vehicles to be painted with it
% is more than the paintBatchLimit.
% nvalue := returns the number of distinct values in the array
constraint 
  if (forall (c in 1..numOfColours)(count(vehicles[..,PaintColour],c) <= paintBatchLimit))=false then 
    forall (i in numPreviousDayVehicles+1..numVehicles-paintBatchLimit)
      (
         nvalue([vehicles[schedule[j] , PaintColour] | j in i..i+paintBatchLimit]) > 1
      ) 
  endif :: domain;
  
% For each contraint...
constraint forall (c in Constraints)
% Count the violations of the current constraint in the whole schedule. Start by setting the starting points
% of the windows you are about to examine, from the first to the last one. For example, if the current constraint ratio
% is N/P, the starting point of the first window is #previousDayVehicles - P + 2, and the starting point of the last window is
% #totalVehicles - N, as indicated in the problem definition.
  (violations[c] = sum(i in (numPreviousDayVehicles - ratios[c,Denominator] + 2)..(numVehicles-ratios[c,Numerator]))
      % For each window (positions i to i+P, unless we reach the end of the schedule), count the number of violations
      % of the current constraint, which is defined as "vehiclesWithConstraint - N". If the number of violations is negative,
      % then it is actually zero (0), so that is the usage of "max".
      (max(0,(
        sum (j in i..min(i+ratios[c,Denominator]-1,numVehicles)) (associatedConstraints[schedule[j],c])
             )-ratios[c,Numerator]
           )
      ) 
  % Domain consistency was too costly for this complex constraint, and so we decided bounds consistency was the way to go.
  ) :: bounds;
  
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    SYMMETRY BREAKING    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% The following function and constraint serve as our symmentry breaking constraint. The function
% "sameAttributes" returns true if two vehicles have the same contraints and paint colour.
% The constraint ensures that all these identical vehicles are ordered within our schedule in
% descending order (in terms of vehicle ID). The reasoning behind this is given in the report.
% The constraint is left commented-out because, for certain big files, it seems to work counter
% productively. More on this in the report.

%function var bool: sameAttributes (var int: v1, var int: v2) = (vehicles[v1,PaintColour] = vehicles[v2,PaintColour] /\ forall (c in Constraints) 
%(associatedConstraints[v1,c] = associatedConstraints[v2,c]));

%constraint symmetry_breaking_constraint(forall (i,j in numPreviousDayVehicles+1..numVehicles where i<j) (sameAttributes(schedule[i],schedule[j]) -> %schedule[i] > schedule[j]));
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%    OPTIMIZATION FUNCTION    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Our optimization function tries to minimize the sum of the 3 decision variables: high-priority constraints, low-priority constraints, and paintChanges.
% Each decision variable is multiplied by its corresponding multiplier, indicating its importance and priority.
% We attempted to use the below search strategy, for reasons explained in the report, but the results were worse than without the use of it.
% :: int_search(schedule, input_order, indomain_max, complete)
solve minimize (highViolations * objectiveMultipliers[HPRatioConstraints]) + (lowViolations * objectiveMultipliers[LPRatioConstraints]) + (paintChanges * objectiveMultipliers[PaintColourBatches]);

output[show(schedule), 
"\n violations: ", show(violations), 
"\n highViolations:", show(highViolations), 
"\n lowViolations:", show(lowViolations), 
"\n totalViolations:", show(sum(violations)), 
"\n paintChanges:", show(paintChanges), 
"\n minimizedGoal:", show((highViolations * objectiveMultipliers[HPRatioConstraints]) + (lowViolations * objectiveMultipliers[LPRatioConstraints]) + (paintChanges * objectiveMultipliers[PaintColourBatches])), 
"\n", show((highViolations * objectiveMultipliers[HPRatioConstraints]) + (lowViolations * objectiveMultipliers[LPRatioConstraints]) + (paintChanges * objectiveMultipliers[PaintColourBatches]))];
