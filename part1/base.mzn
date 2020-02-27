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
array[1..numVehicles] of var 1..numVehicles: schedule;

array[Constraints] of var int: violations;

%number of violations of high priority constraint
var int : highViolations = sum(c in Constraints where ratios[c,Priority] = 1)(violations[c]);

%number of violations of low priority constraint
var int : lowViolations = sum(c in Constraints where ratios[c,Priority] = 0)(violations[c]);

%Support variables

int : numPreviousDayVehicles = count(vehicles[..,Date],0); %Number of vehicles from the previous day

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%%%% The following are our custom constraints

constraint all_different(schedule);


%If a vehicle belongs to date D-1, then its place in the schedule is the same as its place in the "vehicles" array.
%Both versions are kept to test which one is faster
%constraint forall (i in 1..numVehicles) ((vehicles[i,Date]=0 /\ schedule[i] != i) = false);
constraint forall (i in 1..numVehicles) (vehicles[i, Date] = 0 -> schedule[i] = i);

%nvalue = returns the number of distinct values in the array
% This makes sure there are no long sequences of the same paint color
constraint forall (i in numPreviousDayVehicles+1..numVehicles-paintBatchLimit)
  (
     nvalue([vehicles[schedule[j] , PaintColour] | j in i..i+paintBatchLimit]) > 1
  );
 

 % Counts how many times the paint has been changed 
var int : paintChanges;
constraint paintChanges = count(
  [ vehicles[schedule[i], PaintColour] != vehicles[schedule[i+1],PaintColour]  | i in numPreviousDayVehicles+1..numVehicles-1], 
    true
);

%violations[c] is defined as the sum of 0 or 1, where it is 1 if the constraint is not respected for a certain window
constraint forall (c in Constraints)( %For every constraint
  violations[c] = sum(i in numPreviousDayVehicles+1..numVehicles-ratios[c, Denominator])( %For every window
    max(0, min(1,
    (sum(j in i..i+ratios[c, Denominator])(associatedConstraints[schedule[j], c])) - ratios[c, Numerator]
    ))
  )
);


% The multipliers of each available objective, which are described in "Objectives."
% In the original datafile, the first-ranked objective receives a multiplier of 10000,
% the second a multiplier of 100 and the third a multiplier of 1. If an objective is
% not included in the optimization objectives, its multiplier is 0.
array[Objectives] of int: objectiveMultipliers;

solve minimize (highViolations * objectiveMultipliers[HPRatioConstraints]) + (lowViolations * objectiveMultipliers[LPRatioConstraints]) + (paintChanges * objectiveMultipliers[PaintColourBatches]);

output[show(schedule), "\n violations: ", show(violations), "\n highViolations:", show(highViolations), "\n lowViolations:", show(lowViolations), 
"\n totalViolations:", show(sum(violations)), "\n paintChanges:", show(paintChanges), 
"\n minimizedGoal:", show((highViolations * objectiveMultipliers[HPRatioConstraints]) + (lowViolations * objectiveMultipliers[LPRatioConstraints]))];