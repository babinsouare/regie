# CLAUDE.md — API OGP

API REST de la plateforme de régie publicitaire de l'Office Guinéen de la Publicité :
parc de panneaux, campagnes, devis, factures, encaissements, notifications.

**Ce dépôt n'est que le backend.** Un front Angular et deux applications mobiles (une pour les
clients annonceurs, une pour les agents de terrain) vivent ailleurs. Plusieurs contrôleurs n'ont de
sens qu'au regard de ces clients : `ClientAuthController` sert l'app clients, `EvenementController`
l'app agents.

## Architecture

Spring Boot 3.3.3 · Java 17 · Gradle (Kotlin DSL) · PostgreSQL · packaging WAR + JAR.

Découpage en 4 couches, répété pour chaque entité — **toute nouvelle fonctionnalité doit traverser
les quatre** :

```
models/          entités JPA (Lombok @Data) + Result<T>
repository/      Spring Data JPA
services/        interfaces
implementations/ implémentations (@Service)
controllers/     REST
```

Packages transverses : `notifications/` (moteur, scheduler, expéditeurs), `security/` (JWT),
`config/` (DataInitializer, CampagnesScheduler), `dto/`, `utils/`.

**Domaine** : `Campagnes` ↔ `PanneauxCampagne` ↔ `Panneaux` ; géographie à 4 niveaux
`Region → Commune → Quartier → Secteur` ; facturation `Devis → Facture → Paiements` ;
RBAC `Users/Profils/Permissions` avec surcharge par utilisateur (`PermissionUsers`).

**Deux authentifications distinctes** : `/auth/login` (internes, table `users`) et
`/client/auth/login` (clients, jeton `role=CLIENT`). Conséquence majeure :
`SecurityUtils.getCurrentUser()` interroge `users` et **renvoie `null` pour un client connecté**.

## Commandes

```bash
./gradlew compileJava      # compilation
./gradlew bootRun          # démarrage local (port 8080)
./gradlew build            # build complet + tests
./gradlew test             # tests (1 seul : contextLoads)
```

Il n'y a **ni linter ni formateur** configuré.

**Vérification réelle** : `compileJava` ne valide pas les requêtes JPQL ni les query methods
dérivées — seul le démarrage du contexte le fait. Après tout ajout au repository, lancer `bootRun`
et attendre `Started ApiOGPApplication`. Les routes enregistrées se vérifient sur
`GET /v3/api-docs` (public).

**Déploiement** : push sur `main` → GitHub Actions (`.github/workflows/deploy.yml`) construit
l'image Docker, la pousse sur Docker Hub, puis `docker compose up -d` sur le VPS. Attention : le
`Dockerfile` copie `build/libs/*.jar` **sans lancer de build Gradle** — le déploiement dépend du
`build/` commité.

## Conventions

- **Réponses** : toujours `Result.success(...)` / `Result.error(...)`, avec les **trois messages
  FR/EN/PT obligatoires**. Aucune fabrique monolingue n'existe.
- **Codes HTTP** : les erreurs sont portées par `status` dans le corps, avec un HTTP 200. Choix
  assumé, cohérent dans tout le projet — ne pas « corriger ».
- **Pas de SQL natif dans les ajouts.** Écrire en JPQL ou en query method dérivée, `Pageable`
  plutôt que `LIMIT`. La base est PostgreSQL mais une cible SQL Server a été évoquée ; les requêtes
  natives préexistantes (`TO_CHAR`, `DATE_TRUNC`, `LIMIT`) ne survivraient pas à une migration.
- **Dates** : format `yyyy-MM-dd` via `@DateTimeFormat` pour les nouveaux endpoints. Normaliser les
  bornes en Java avec `Helpers.debutDeJournee` / `finDeJournee` (bornes inclusives) plutôt qu'un
  `DATE()` SQL. Les endpoints anciens utilisent `dd/MM/yyyy` ou `dd-MM-yyyy` — ne pas y toucher.
- **Nommage** : français pour le métier, préfixe `bt` pour les booléens (`btEnabled`, `btValide`),
  `dtCreated` / `dtLastUpdate` sur les entités, endpoints en minuscules collées (`getbyid`,
  `getbyclient`).
- **Transactions** : une logique qui écrit sur plusieurs tables va dans un `@Service`, jamais dans
  un contrôleur appelé depuis un `@Component` planifié — le proxy Spring ne s'appliquerait pas.
- **Schedulers** : chaque élément traité dans son propre `try/catch`, une erreur ne doit pas
  interrompre le balayage.
- `spring.jpa.hibernate.ddl-auto=update` : le schéma suit les entités, **il n'y a pas de
  migrations** (ni Flyway ni Liquibase).

## État du projet

*(section mise à jour en fin de session — voir « Tenue de ce fichier »)*

**Dernière mise à jour : 2026-09-09** · dernier commit `b194932 Integration SOutra`

### Fait
- Socle métier complet : parc, géolocalisation 4 niveaux, tarifs, campagnes (workflow de statuts +
  historique daté), clients, devis, factures, paiements, dashboard KPI, permissions, trilingue.
- Recherches par période (factures création/échéance, campagnes), 10 dernières factures/paiements,
  comptage de panneaux par type de localité.
- Statistiques d'événements : par agent, par type, croisement agent × type (période optionnelle).
- **Module notifications** : 2 tables, 13 endpoints `/notification`, 9 déclencheurs (4 immédiats
  branchés sur le flux métier, 5 planifiés), journal anti-doublon, 11 règles pré-remplies.
- **Intégration Soutra** : `addPaiement` à deux chemins (Cash imputé immédiatement / autres modes
  initiés chez Soutra sans toucher la facture), callback fonctionnel, rattrapage planifié +
  `POST /paiement/verifier`, imputation factorisée dans `PaiementSoutraService`.
- `CampagnesScheduler` : `paied → ongoing` à la date de début, `ongoing → ended` après la date de
  fin, avec rattrapage direct `paied → ended` si la période est entièrement passée.

### En attente de décision
- **Statut `confirmed`** : 4 campagnes l'utilisent, absent de `DataInitializer`. À intégrer au
  référentiel ou à requalifier.
- **Nettoyage base** : `pending_date` subsiste en base (supprimé du code, aucune campagne ne
  l'utilise) et les ordres de `ongoing/ended/canceled/archived` sont décalés de +1.
  `initStatut` ne met jamais à jour l'existant, donc l'écart ne se corrigera pas seul.

### Non vérifiable en l'état
- Chemin Cash et initiation Soutra depuis `addPaiement` : exigent un JWT valide.
- Rattrapage Soutra : la table `soutra_config` est **vide**, l'intégration n'est pas paramétrée.
- Notifications : les 11 règles sont **désactivées** et les 3 expéditeurs **simulés** (ils
  journalisent au lieu d'envoyer). Volontaire — ne pas activer sans demande explicite.

### Dette connue (signalée, non traitée)
- Secrets de production en clair dans `application.properties` et `SecParans.SECRET`, versionnés.
- `SoutraConfig.clientSecret` / `apiKey` renvoyés en clair par `GET /soutra-config/liste`
  (manque `@JsonProperty(access = WRITE_ONLY)`, comme sur `Clients.password`).
- `JWTAuthorizationFilter` ne pose aucune autorité : tout utilisateur authentifié atteint tous les
  endpoints, le contrôle d'accès est purement côté client.
- Pas de `.gitignore` ; 239 fichiers de `build/`, `.gradle/`, `.idea/` sont suivis par git.
- Un seul test (`contextLoads`). `allow-circular-references=true` masque des cycles de beans.

## Décisions techniques

*(section mise à jour en fin de session)*

- **Requêtes portables (JPQL)** — la base est PostgreSQL mais SQL Server a été évoqué comme cible ;
  écrire portable coûte peu et rend la question sans conséquence.
- **Erreurs en HTTP 200** — antérieur, conservé par cohérence de tout le contrat d'API.
- **Journal de notifications comme garde anti-doublon** — le triplet
  `(règle, objet, canal)` est vérifié avant chaque envoi. Sans lui, un rappel « J-7 » repartirait à
  chaque balayage quotidien : sept SMS au client.
- **Deux requêtes par agrégation d'événements** (bornée / non bornée) — un encadrement par bornes
  larges écarterait silencieusement les événements sans `dateEvenement`, colonne nullable.
- **Règles de notification créées désactivées, envois simulés** — activer d'office des relances
  vers de vrais annonceurs serait dangereux, et la plateforme SMS DBA n'est pas raccordée. Pour
  brancher le SMS réel : ne changer que le corps de `SmsSenderSimule.envoyer`.
- **Une notification ne casse jamais une opération métier** — tous les appels au moteur sont sous
  `try/catch`, la campagne ou le paiement se termine normalement en cas d'échec d'envoi.
- **Soutra : la facture n'est imputée qu'au retour** (callback ou `checkStatus`), jamais à
  l'initiation. Le callback porte déjà le statut, donc aucun appel de confirmation supplémentaire ;
  `checkStatus` ne sert qu'au rattrapage quand le callback ne vient pas.
- **`reference_objet` sans clé étrangère** dans le journal — elle pointe vers une campagne *ou* une
  facture selon le déclencheur, ce qu'une FK ne sait pas faire.
- **`SecurityUtils.getCurrentUser()` nul toléré hors Cash** — un client payant depuis son mobile
  n'existe pas dans `users`. Le Cash l'exige (écriture manuelle engageant un agent), Soutra non.
- **Sémantique des statuts de campagne** — `paied` = intégralement réglée ; `ongoing` = dans
  l'intervalle de dates. Ne pas confondre avec `statutPaiement` où `ongoing` signifie
  « paiement partiel ». `pending_date` est supprimé.

## Tenue de ce fichier

À la fin de chaque session de travail significative, mettre à jour **« État du projet »** et
**« Décisions techniques »** sans attendre qu'on le demande : déplacer ce qui est terminé, ajouter
les décisions structurantes et leur justification, retirer ce qui n'est plus vrai. Garder les
sections courtes — uniquement ce qui change la façon de travailler sur ce dépôt.
