# C2IMPRESS: CO-CREATIVE IMPROVED UNDERSTANDING AND AWARENESS OF MULTI-HAZARD RISKS FOR DISASTER RESILIENT SOCIETY

C2IMPRESS is a European project that supports communities in responding to multiple hazards, such as floods, wildfires, and earthquakes, through integrated, people- and place-centered frameworks. This repository contains the Agent-Based Modeling (ABM) and Human Behavior Modeling (HBM) simulations used to study evacuation dynamics and risk awareness in multiple case study areas (CSAs) under real and hypothetical disaster scenarios.

## How to Run the Simulations

This simulation system builds upon the Evacuation Experimentation System (EES): https://github.com/agentsoz/ees

### Dependencies

This program depends on the following projects:
- EES – Evacuation Experimentation System
- BDI-ABM Integration
- Social Network Diffusion Model


## How to run

To run the example scenario unzip the release archive and follow the instructions provided in the packaged README.md.


Clone the repository and initialize submodules:


If already cloned:

git submodule update --init --recursive

### How to Build

./mvnw package

This will produce the EES release archive at:  
ees/target/ees-x.y.z-SNAPSHOT.zip

### How to Run

Unzip the release archive:

unzip ees/target/ees-x.y.z-SNAPSHOT.zip

Then follow the instructions inside the unzipped folder’s README to run specific simulations.

## View Simulation Results

Simulation outputs are provided as HTML visualizations under the /docs/ directory. These include evacuation maps, route congestion, population flows, and zone capacities for each scenario.

### Egaleo, Greece – Earthquake

Scenario folders:  
ees/scenarios/EgaleoCSA-Earthquack/Scenario1  
ees/scenarios/EgaleoCSA-Earthquack/Scenario2  
ees/scenarios/EgaleoCSA-Earthquack/Scenario3  
ees/scenarios/EgaleoCSA-Earthquack/Scenario4  

HTML results:  
docs/egaleo/scenario1.html  
docs/egaleo/scenario2.html  
docs/egaleo/scenario3.html  
docs/egaleo/scenario4.html

### Mallorca, Spain – Wildfire (Bushfire)

Scenario folders:  
ees/scenarios/MallorcaCSA-Bushfire/Scenario1  
ees/scenarios/MallorcaCSA-Bushfire/Scenario2  

HTML results:  
docs/mallorca_bushfire/scenario1.html  
docs/mallorca_bushfire/scenario2.html

### Mallorca, Spain – Flash Flood

Scenario folders:  
ees/scenarios/MallorcaCSA-FlashFlood/Scenario1  
ees/scenarios/MallorcaCSA-FlashFlood/Scenario2  
ees/scenarios/MallorcaCSA-FlashFlood/Scenario3  
ees/scenarios/MallorcaCSA-FlashFlood/Scenario4  

HTML results:  
docs/mallorca_flood/scenario1.html  
docs/mallorca_flood/scenario2.html  
docs/mallorca_flood/scenario3.html  
docs/mallorca_flood/scenario4.html

## Acknowledgments

Developed as part of the C2IMPRESS project by RMIT Europe, RMIT University, Université Grenoble Alpes, METEO France, TVS, and local municipalities. Simulations are based on and extend the open-source EES platform.

## License

This project is released under the MIT License.

## Contact

For questions or collaboration:  
Hossein.moradi@rmit.edu.au



