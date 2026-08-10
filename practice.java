package com.codeforge.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * SkillVector represents multi-dimensional candidate or target role competencies.
 * Evaluates semantic gap and cosine similarity between candidate and requirement vectors.
 */
public class SkillVector {
    private final Map<String, Double> dimensions;

    public SkillVector() {
        this.dimensions = new HashMap<>();
    }

    public SkillVector(Map<String, Double> dimensions) {
        this.dimensions = new HashMap<>(dimensions);
    }

    public void setSkillWeight(String skill, double weight) {
        dimensions.put(skill.toLowerCase().trim(), Math.min(1.0, Math.max(0.0, weight)));
    }

    public double getSkillWeight(String skill) {
        return dimensions.getOrDefault(skill.toLowerCase().trim(), 0.0);
    }

    public Set<String> getSkills() {
        return dimensions.keySet();
    }

    public Map<String, Double> getDimensions() {
        return new HashMap<>(dimensions);
    }

    /**
     * Calculates Cosine Similarity between this vector and target requirement vector.
     */
    public double cosineSimilarity(SkillVector target) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        Set<String> allSkills = getSkills();
        for (String skill : allSkills) {
            double valA = this.getSkillWeight(skill);
            normA += valA * valA;
        }

        for (String skill : target.getSkills()) {
            double valB = target.getSkillWeight(skill);
            normB += valB * valB;
            if (this.dimensions.containsKey(skill)) {
                dotProduct += this.getSkillWeight(skill) * valB;
            }
        }

        if (normA == 0.0 || normB == 0.0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    /**
     * Computes missing or under-skilled deltas required to reach target competency.
     */
    public Map<String, Double> calculateGap(SkillVector target) {
        Map<String, Double> gapMap = new HashMap<>();
        for (Map.Entry<String, Double> entry : target.getDimensions().entrySet()) {
            String skill = entry.getKey();
            double targetScore = entry.getValue();
            double currentScore = getSkillWeight(skill);
            if (currentScore < targetScore) {
                gapMap.put(skill, targetScore - currentScore);
            }
        }
        return gapMap;
    }
}
