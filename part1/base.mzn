include "globals.mzn";

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

% The limit on the upper batch size of paint colour batches.
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

%%%% The following are our custom variables to be "solved"
array[1..numVehicles] of var int: schedule;

array[Constraints] of var int: violations;

var int : highViolations = sum(c in Constraints where ratios[c,Priority] = 1)(violations[c]);

var int : lowViolations = sum(c in Constraints where ratios[c,Priority] = 0)(violations[c]);

var int : paintViolations;

%%%% The following are our custom constraints

%If a vehicle belongs to date D-1, then its place in the schedule is the same as its place in the "vehicles" array.
constraint forall (i in 1..numVehicles) ((vehicles[i,Date]=0 /\ schedule[i] != i) = false);

int : numPreviousDayVehicles = count(vehicles[..,Date],0);

constraint all_different(schedule);

%For each contraint
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
  );
  

% The multipliers of each available objective, which are described in "Objectives."
% In the original datafile, the first-ranked objective receives a multiplier of 10000,
% the second a multiplier of 100 and the third a multiplier of 1. If an objective is
% not included in the optimization objectives, its multiplier is 0.
array[Objectives] of int: objectiveMultipliers;

solve minimize (highViolations * objectiveMultipliers[HPRatioConstraints]) + (lowViolations * objectiveMultipliers[LPRatioConstraints]);

output[show(schedule), "\n violations: ", show(violations), "\n highViolations:", show(highViolations), "\n lowViolations:", show(lowViolations), 
"\n totalViolations:", show(sum(violations)), 
"\n minimizedGoal:", show((highViolations * objectiveMultipliers[HPRatioConstraints]) + (lowViolations * objectiveMultipliers[LPRatioConstraints]))];