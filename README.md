# MiniJeu

## Auteurs : 
- Baptise N’DIAYE
- Tom ROMANO

## Description :
Le miniJeu est un jeu développé en Java. Le but du jeu est d'empêcher une balle de toucher les bords de l'écran. La balle commence au centre de l'écran et se déplace régulièrement dans une des quatre directions possibles : haut, bas, gauche, droite. À chaque clic sur l'écran, la balle change de direction et accélère. Le jeu se termine lorsque la balle atteint le bord de l'écran, et affiche le score sous forme de temps de survie en secondes.

## Fonctionnalités supplémentaires :
- **Secouer pour ralentir la balle** : L'utilisateur peut secouer l'appareil pour ralentir la balle, à condition que celle-ci ne soit pas déjà trop lente.
- **Dire des virelangues** : En fonction de la difficulté, dire un virelangue permet de ralentir la balle.
- **Pages de résultat** : Après chaque partie, une page de résultat affiche le score et les statistiques de la partie.

## Capteurs utilisés :
- **Acceleromètre** : Utilisé pour secouer l'appareil et ralentir la balle.
- **Microphone** : Utilisé pour la reconnaissance vocale afin de détecter les virelangues et ralentir la balle.

## Activités principales :
- **MainActivity** : L'activité principale du jeu où se déroule la partie.
- **StartActivity** : L'activité de démarrage qui permet à l'utilisateur de commencer une nouvelle partie.
- **ResultActivity** : L'activité qui affiche le résultat de la partie une fois que la balle touche le bord de l'écran.

## SharedPreferences :
Les résultats des jeux sont enregistrés dans **SharedPreferences** avec les clés suivantes :
- **totalGamePlayed** : Le nombre total de jeux joués.
- **results** : Les résultats des parties précédentes.



## Démo :
Une vidéo de démonstration du jeu est disponible dans le dossier "demos".

