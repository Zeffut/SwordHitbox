# Minecraft 26.3

## Build

JDK 25 (JDK 21 reste nécessaire pour compiler les anciennes cibles 1.21.x) :

```sh
./gradlew :26.3-fabric:build :26.3-neoforge:build
```

Les JAR de distribution sont dans `versions/26.3-{fabric,neoforge}/build/libs/`.
Les sources et les classes de test ne doivent pas être livrées comme mod.

## Changements

- Fabric 26.3 utilise **Loom sans remappage** (`net.fabricmc.fabric-loom`), et non des mappings d'identité. Ces derniers compilent mais font échouer les class tweakers de Fabric API au lancement.
- Les deux loaders utilisent `ModernHitboxRenderer` : la phase d'extraction produit une liste immuable de boîtes et segments relatifs à la caméra ; la phase de soumission ne lit ni le monde, ni les entités, ni la configuration mutable.
- Fabric attache les données à `LevelRenderState` via `RenderStateDataKey` ; NeoForge via `ContextKey` et ses événements `ExtractLevelRenderStateEvent` / `SubmitCustomGeometryEvent`.
- Le vecteur de regard est soumis comme une véritable ligne de deux mètres, pas comme une boîte englobante.
- Les chemins de rendu des versions antérieures restent séparés par Stonecutter.

## Test d'intégration dans un vrai client

Créer une fois un monde solo jetable nommé **New World** dans le répertoire `run/saves` de la cible. Il est possible de copier ce monde entre les deux cibles. Ne jamais utiliser une sauvegarde personnelle : le test modifie la configuration et l'objet tenu.

```sh
./gradlew :26.3-fabric:runClient -PswordhitboxSmoke
./gradlew :26.3-neoforge:runClient -PswordhitboxSmoke
```

Sur un serveur X11 virtuel avec rendu logiciel :

```sh
DISPLAY=:99 SDL_VIDEODRIVER=x11 LIBGL_ALWAYS_SOFTWARE=1 \
  ./gradlew :26.3-fabric:runClient -PswordhitboxSmoke
```

Les probes sont dans `src/smokeTest/`. Elles ne sont ajoutées au classpath et aux métadonnées que lorsque la propriété `swordhitboxSmoke` est présente. Le test charge le monde, ajoute un zombie client jetable et vérifie :

1. Aucune géométrie sans épée.
2. Aucune géométrie si le mod est désactivé.
3. Exclusion du joueur local en mode joueurs uniquement.
4. Extraction du corps, de la bande des yeux et du vecteur de regard.
5. Application de la couleur de cible.
6. Vecteur de regard représenté par un segment de deux mètres.
7. Instantané d'extraction immuable.
8. Soixante images de soumission dans le pipeline réel sans exception.

Le succès exige `SWORDHITBOX_SMOKE_COMPLETE` **et** une sortie Gradle réussie. Un lancement au seul menu principal ne valide pas ce test. Les probes ne remplacent pas une validation visuelle et multijoueur sur le matériel d'un joueur.

Après les tests, reconstruire **sans** `-PswordhitboxSmoke` et vérifier que les JAR ne contiennent aucune classe `SmokeProbe`, ni mod/entrypoint de test. La CI compile aussi les probes des deux nouvelles cibles, mais ne lance pas automatiquement de monde graphique.
