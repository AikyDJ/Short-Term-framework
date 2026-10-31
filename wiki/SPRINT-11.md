# Implemetation sprint 11
## Framework
#### pourquoi ?
On veut implementer un singleton. c quoi vue que le sprint 10 a ete skip . o par toujour de App initsialaizer. quant on on inicilalise le frane work on recupre les classe ayant les anotaion controller dans le projet du developeur donc au lieu de achaque fois chercher la class dans routmappin on l'initialise 1 fois puis si les requet suivante sont au meme controller on re utilise la meme instance pour optimiser la vitesse des requet
#### MAJ
- Ajout de `ControllerRegitery` qui garde la dernier instance des classe controller utiliser
 