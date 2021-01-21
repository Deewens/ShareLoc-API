# Projet ShareLoc : API JAX-RS
Shareloc est une plateforme permettant d'échanger des services au sein d'une colocation.

**Une documentation des routes est accessible au format raml [ici](http://cdad151.iutrs.unistra.fr/doc/).**

L'api est accessible à l’adresse suivante : http://cdad151.iutrs.unistra.fr:8080/ShareLoc-API-1.0-SNAPSHOT/api/

- Auteurs : Nemanja Alabic et Adrien Dudon

## Techniques et fonctionnalités clés

L'API JAX RS utilisé est Jersey et Maven a été utilisé pour l'ajout de package au projet.
La version de Java Enterprise utilisé est Jakarta EE 8 à l'aide de Java 11.
L'API est ensuite hébergé sur un serveur GlassFish (Payara 5).

### Gestion des erreurs
Afin de pouvoir gérer les erreurs le plus simplement possible, les Beans Validations ont été utilisés. Il est
donc possible en rajoutant une simple annotation (@Valid) sur une entité d'effectuer des vérifications automatiques sur
l'entité et de renvoyer les messages d'erreurs adéquates à l'utilisateur sans avoir à le faire soit-même.  

### Cryptage du mot de passe
Les mots de passes stockés en base sont cryptés et un salt a été utilisé pour éviter les collisions.




