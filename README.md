# Tropico

Projet Tropico par OGER Corentin et DOS SANTOS Sébastien.

Licence libre.

Pour compiler le projet, il suffit de lancer un ide tel qu'Eclipse ou encore IntelliJ, et lancer via le boutton prévu à cet effet.

Le jeu fonctionnera ensuite par ligne de commandes. Les options possibles apparaissent à l'écran, vous pouvez choisir celle que vous voulez en choisissant un nombre écrit devant chaque option.

Avant de lancer une partie, il faudra renseigner le nombre de joueur, le mode de jeu ainsi que la difficulté. Tout cela via l'interface évidemment.
Vous pouvez aussi sauvegarder une partie. Pour ce faire, dès que le choix se présente, Inscrivez 0 puis Entrer dans la console. Vous pouvez ensuite quitter le jeu, soit en fermant la console, soit en saisissant -1 (disponible à tout moment pour quitter proprement l'application.)
Pour lancer la partie il vous suffit de relancer l'application, choisir "Charger une partie", puis sélectionner le mode de jeu. Vous serez de retour sur votre partie.

Il est intéressant de noter qu'il est possible facilement pour un utilisateur de rajouter des évennements, voire des scénarios entiers. Pour ce faire, deux solutions :
- Créer un fichier json dans le répertoire "scénarios" lancer le script python disponible dans les ressources, et sélectionner votre fichier. Vous pourrez ensuite créer des évennements dans la console. Déplacer ensuite le fichier json dans un répertoire de scénario en le renommant "events.json".
- Modifier un fichier json existant dans les scénarios.

Pour ce qui est des scénarios, il suffit de rajouter dans le fichier "scenarios.txt" le nom du scénario suivi de ":" puis du nom du répertoire dans le dossier scénarios. Ce répertoire doit contenir trois fichiers json :
- Un fichier contenant les ressources de base d'un joueur.
- Un fichier contenant le nom des différentes factions et leurs statistiques de base.
- Un fichier contenant les évènnements, comme vu un peu plus haut.

Si vous rencontrer des bugs, ce qui peut arriver, nous sommes vraiment désolé du désagrément, et nous tenterons de les corriger dans les plus brefs délais.

Liste des bugs connus à ce jour:
- Si une sauvegarde est effectué pendant la fin de l'année, le joueur sera remis face à un évennement au lieu de se retrouver face aux choix de fin d'année.
- Les choix faisant perdre de l'argent ou de la nourriture peuvent être choisi, laissant la ressource à 0 au lieu d'être indisponible.