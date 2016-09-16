# javagp [![Build Status](https://travis-ci.org/pucrs-automated-planning/javagp.svg?branch=master)](https://travis-ci.org/pucrs-automated-planning/javagp)
Java Implementation of Graphplan

## How to use

### Build

In order to use JavaGP you can either download a self-contained jar or build it from scratch.

To download the latest version, just go [here](https://github.com/guilhermekrz/javagp/releases/latest) and get the "javagp.jar" file.

In order to build it, you need to have Java (http://java.sun.com) Ant (http://ant.apache.org/) configured in your system, to run the build script build.xml. Then, in order to run the planner you need to follow these steps:

Go to the root of the JavaGP distribution (cd <JavaGP>)

Build the project (ant)

### Run
 
For PDDL Language, run
```bash
java -jar javagp.jar -d examples/pddl/blocksworld/blockworlds.pddl -p examples/pddl/blocksworld/pb1.pddl
```

For STRIPS Language, run 
```bash
java -jar javagp.jar -nopddl -d examples/strips/ma-prodcell/domain.txt -p examples/strips/ma-prodcell/problem.txt
```

#### Planner arguments
```bash
-maxlevels <NUMBER>,	                Max Graph levels.
-timeout <NUMBER>,                      Planning timeout.
-extractAllPossibleSolutions <NUMBER>,  Extract all possible solutions with length up to <NUMBER> beyond optimal plan

-noHeuristics,			No Heuristics.

[Heuristics for actions]
-operatorsLatest,	Select actions that appears latest in the Planning Graph.
or
-noopsFirst, 		Select Noops first.

[Heuristic for propositions]
-propositionsSmallest,	Select firstly propositions that leads to the smallest set of resolvers.
or
-sortGoals,				Sort goals by proposition that appears earliest in the Planning Graph.

[JavaGP Default Heuristics]
-operatorsLatest
-propositionsSmallest
```
