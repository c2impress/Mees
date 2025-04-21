# C2IMPRESS: CO-CREATIVE IMPROVED UNDERSTANDING AND AWARENESS OF MULTI-HAZARD RISKS FOR DISASTER RESILIENT SOCIETY

C2IMPRESS is a European project that supports communities in responding to multiple hazards, such as floods, wildfires, and earthquakes, through integrated, people- and place-centered frameworks. This repository contains the Agent-Based Modeling (ABM) and Human Behavior Modeling (HBM) simulations used to study evacuation dynamics and risk awareness in multiple case study areas (CSAs) under real and hypothetical disaster scenarios.

## How to Run the Simulations

This simulation system builds upon the Evacuation Experimentation System (EES): https://github.com/agentsoz/ees

### Dependencies

This program depends on the following projects:
- MEES – MultiModal Evacuation Experimentation System
- BDI-ABM Integration
- Social Network Diffusion Model


## How to run

To run the example scenario unzip the release archive and follow the instructions provided in the packaged README.md.


Clone the repository and initialize submodules:


If already cloned:

```
git submodule update --init --recursive
```

### How to Build

```
./mvnw package
```

This will produce the EES release archive at:  
ees/target/ees-x.y.z-SNAPSHOT.zip

### How to Run

Unzip the release archive:

unzip ees/target/ees-x.y.z-SNAPSHOT.zip

Then follow the instructions inside the unzipped folder’s README to run specific simulations.

## View Simulation Scenarios


### Egaleo, Greece – Earthquake

Scenario folders:  
```
ees/scenarios/EgaleoCSA-Earthquack
```

### Mallorca, Spain – Wildfire (Bushfire)

Scenario folders:  
```
ees/scenarios/MallorcaCSA-Bushfire
```


### Mallorca, Spain – Flash Flood

Scenario folders:  
```
ees/scenarios/MallorcaCSA-FlashFlood
```


## Contact

For questions or collaboration:  
Hossein.moradi@rmit.edu.au



