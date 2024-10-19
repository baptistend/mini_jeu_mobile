package com.ndiaye.baptisye.minijeu;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Virelangues {

    // Map pour associer les virelangues à leur niveau
    private static Map<Integer, List<String>> virelanguesParNiveau;

    // Bloc statique pour initialiser la map de virelangues par niveau
    static {
        virelanguesParNiveau = new HashMap<>();
        ajouterVirelangues();
    }

    // Méthode pour ajouter les virelangues et les associer à un niveau
    private static void ajouterVirelangues() {
        ajouterVirelangueAuNiveau(0, "serge cherche à changer son siège");
        ajouterVirelangueAuNiveau(0, "la mouche rousse touche la mousse");

        // Niveau 1
        ajouterVirelangueAuNiveau(1, "je sèche ses cheveux chez ce cher serge");
        ajouterVirelangueAuNiveau(1, "un chasseur sachant chasser doit savoir chasser sans son chien");

        // Niveau 2
        ajouterVirelangueAuNiveau(2, "pour qui sont ces serpents qui sifflent sur vos têtes");
        ajouterVirelangueAuNiveau(2, "le chat sauvage se sauve le chasseur chauve la chasse");

        // Niveau 3
        ajouterVirelangueAuNiveau(3, "je suis un juge juste je n'ai juste jamais jugé");
        ajouterVirelangueAuNiveau(3, "le chameau s'acharne a charmer la chamelle la chamelle à chiner le chamois");

        // Niveau 4
        ajouterVirelangueAuNiveau(4, "la cocarde pique coco le coq au cucul cocorico son coccyx pique");
        ajouterVirelangueAuNiveau(4, "tache de tracer la tranchée et traverse sans te trancher la trachée");


    }

    // Méthode pour ajouter un virelangue à un niveau donné
    private static void ajouterVirelangueAuNiveau(int niveau, String virelangue) {
        virelanguesParNiveau.computeIfAbsent(niveau, k -> new ArrayList<>()).add(virelangue);
    }

    // Méthode pour obtenir les virelangues d'un niveau spécifique
    public static List<String> getVirelanguesParNiveau(int niveau) {
        return virelanguesParNiveau.getOrDefault(niveau, new ArrayList<>());
    }

    // Méthode pour afficher les virelangues par niveau
    public static void afficherVirelanguesParNiveau(int niveau) {
        List<String> virelangues = getVirelanguesParNiveau(niveau);
        if (virelangues.isEmpty()) {
            System.out.println("Aucun virelangue pour ce niveau.");
        } else {
            System.out.println("Niveau " + niveau + " :");
            for (String virelangue : virelangues) {
                System.out.println(virelangue);
            }
        }
    }

    // Méthode pour vérifier si un string correspond à un virelangue
    public static int trouverNiveauVirelangue(String phrase) {
        Log.w("phrase ", phrase);
        return virelanguesParNiveau.entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(phrase)) // Filtrer les niveaux qui contiennent la phrase
                .map(Map.Entry::getKey) // Récupérer le niveau
                .findFirst() // Trouver le premier niveau correspondant
                .orElse(-1);// Retourne -1 si aucune correspondance
    }
}