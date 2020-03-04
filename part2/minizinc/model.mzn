include "globals.mzn";

% --- Parameters ---
int: numJobs; % Number of jobs.
int: numAgents; % Number of agents.
array[1..numJobs] of int: processingTimes; % The processing time of each job.

% The preferred schedule of each agent. It is an ordered list, i.e. if preferences[a,j] = x,
% then that means that agent a wants to schedule job x at the j-th place.
array[1..numAgents, 1..numJobs] of 1..numJobs: preferences;

% --- Pre-computed parameters ---
% The completion time of each job in "preferences". The completion time agentCompletionTimes[a, j] 
% corresponds to the completion time of preferences[a, j] in the preferred schedule of agent a.
array[1..numAgents, 1..numJobs] of int: agentCompletionTimes = array2d(1..numAgents, 1..numJobs, 
  [sum(sumJ in 1..j)(processingTimes[preferences[a, sumJ]]) | a in 1..numAgents, j in 1..numJobs]);

% --- Decision variables ---
array[1..numJobs] of var 1..numJobs: schedule; % The schedule of the number of jobs.

% --- Helper decision variables ---
% The completion time given the "schedule" variable, where completionTimes[j] is the completion time
% for job j.
array[1..numJobs] of var int: completionTimes;

% --- Constraints ---
constraint alldifferent(schedule);

% Calculate the completionTimes sequentially.
constraint completionTimes[schedule[1]] == processingTimes[schedule[1]];
constraint forall(j in 2..numJobs)(completionTimes[schedule[j]] == completionTimes[schedule[j-1]] + processingTimes[schedule[j]]);

% --- Objective ----
var int: tardiness = sum(a in 1..numAgents, j in 1..numJobs)(max(0, completionTimes[preferences[a, j]]-agentCompletionTimes[a, j]));
solve minimize tardiness;