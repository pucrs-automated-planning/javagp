# javagp [![Build Status](https://travis-ci.org/pucrs-automated-planning/javagp.svg?branch=master)](https://travis-ci.org/pucrs-automated-planning/javagp)
Java Implementation of Graphplan

## How to use

### Build

In order to use JavaGP you can either download a self-contained jar or build it from scratch.

To download the latest version, just go [here](https://github.com/pucrs-automated-planning/javagp/releases/latest) and get the "javagp.jar" file.

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

Note that JavaGP does use a substantial amount of memory, so a user intending to work with larger planning problems should become familiar with the memory allocation settings of the Java VM. Namely, one should play with the ``-Mmx2g`` switch to set maximum memory (e.g.  to 2 gigabytes) of RAM. 

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

### Dependencies

JavaGP uses three external libraries (which are included under [lib](lib):
- [Jason](https://github.com/jason-lang/jason) - for the unification algorithm and logic related functionality
- [JavaCC](https://java.net/projects/javacc) - for the parsers internals
- [PDDL4J](https://github.com/pellierd/pddl4j) - for the latest PDDL parser

## PDDL Support

JavaGP supports a relatively small subset of PDDL's ``:requirements``, namely, JavaGP will run correctly (and without generating warnings) with the following requirements:

- ``:strips `` - this is the most basic level it supports, it does so fairly well
- ``:typing`` - supported since migrating to [PDDL4J](https://github.com/pellierd/pddl4j)
- ``:negative-preconditions`` - the planner supports it with a caveat that right now there is a bug in this support

JavaGP **does not** currently support the following, with the following notes (do let me know if I forgot something here):

- ``:disjunctive-preconditions`` - no current support, but we may do operator translations soon
- ``:equality`` -  no current support, but we are studying implementing this in operator instantiation. The current implementation implicitly assumes that variables with different names are always different 
- ``:existential-preconditions``, ``:universal-preconditions``, and ``:quantified-preconditions`` - no quantifiers are currently supported. There may be a way to translate them into something the planning graph can handle when we make the planner ground
- ``:conditional-effects`` - no support, but the same type of translation for disjunctive preconditions may work here
- ``:fluents``, ``:action-costs``, ``:numeric-fluents``, ``:object-fluents``, and ``:adl`` - none of these are supported since it would require planning with numerical objects, and we currently do not plan on supporting this, at least in a GraphPlan implementation
- ``:goal-utilities`` - not implemented, but this should be a hard feature to implement naively in the solution extraction algorithm
- ``:durative-actions``, ``:timed-initial-literals``, ``:duration-inequalities``, and ``:continuous-effects`` - vanilla GraphPlan does not support reasoning about time or durations, so we do not support it
- ``:derived-predicates`` -  although we do not currently support this, it seems to me that this would not be hard to add to the graph expansion algorithm (but it would make graph construction really expensive)
- ``:preferences`` and ``:constraints`` - although we do not support these keys at the moment, it does not seem impossible to create a specialized  [SolutionExtractionVisitor](src/graphplan/graph/algorithm/SolutionExtractionVisitor.java) that would handle this.

