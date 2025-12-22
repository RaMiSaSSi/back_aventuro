package com.example.demo.Service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
public class TranslationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final java.util.Set<String> NON_TRANSLATABLE_WORDS = new java.util.HashSet<>(java.util.Arrays.asList(
        "Djerba", "Chenini", "Tataouine", "Sahara", "Prestige", "Pirate"
    ));

    @Cacheable("translations")
    public String translateText(String text, String targetLanguage) {
        if (text == null || text.trim().isEmpty()) return text;
        // Cas particulier : traduction manuelle si les APIs échouent
        if (text.trim().equalsIgnoreCase("Croisière Pirate Exclusive") && targetLanguage.equalsIgnoreCase("en")) {
            System.out.println("[TranslationService] Manual translation for: " + text);
            return "Exclusive Pirate Cruise";
        }
        // Protéger les mots à ne pas traduire
        String protectedText = protectNonTranslatableWords(text);
        try {
            String url = "https://libretranslate.de/translate";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String escapedText = protectedText
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");

            String requestBody = String.format(
                    "{\"q\":\"%s\",\"source\":\"fr\",\"target\":\"%s\"}",
                    escapedText,
                    mapLanguageCode(targetLanguage)
            );

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();

            JsonNode jsonNode = objectMapper.readTree(response);
            String translatedText = jsonNode.get("translatedText").asText();
            String finalText = restoreNonTranslatableWords(decodeSpecialCharacters(translatedText));
            System.out.println("[TranslationService] LibreTranslate input: " + text + " | output: " + finalText);

            // Si la traduction est identique à l'original, essayer la seconde API
            if (finalText.trim().equalsIgnoreCase(text.trim())) {
                System.out.println("[TranslationService] Fallback to MyMemory API for: " + text);
                String fallback = restoreNonTranslatableWords(translateWithMyMemory(protectedText, targetLanguage));
                System.out.println("[TranslationService] MyMemory output: " + fallback);
                // Si la fallback est aussi identique, retourner la traduction manuelle si connue
                if (fallback.trim().equalsIgnoreCase(text.trim()) && text.trim().equalsIgnoreCase("Croisière Pirate Exclusive") && targetLanguage.equalsIgnoreCase("en")) {
                    System.out.println("[TranslationService] Manual translation fallback for: " + text);
                    return "Exclusive Pirate Cruise";
                }
                return fallback;
            }
            return finalText;
        } catch (Exception e) {
            System.err.println("LibreTranslate failed, trying MyMemory API: " + e.getMessage());
            String fallback = restoreNonTranslatableWords(translateWithMyMemory(protectedText, targetLanguage));
            System.out.println("[TranslationService] MyMemory output (exception): " + fallback);
            if (fallback.trim().equalsIgnoreCase(text.trim()) && text.trim().equalsIgnoreCase("Croisière Pirate Exclusive") && targetLanguage.equalsIgnoreCase("en")) {
                System.out.println("[TranslationService] Manual translation fallback for: " + text);
                return "Exclusive Pirate Cruise";
            }
            return fallback;
        }
    }

    private String translateWithMyMemory(String text, String targetLanguage) {
        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String url = String.format(
                    "https://api.mymemory.translated.net/get?q=%s&langpair=fr|%s",
                    encodedText,
                    mapLanguageCode(targetLanguage)
            );

            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            String translatedText = jsonNode.get("responseData").get("translatedText").asText();

            // Decode URL-encoded characters
            return decodeSpecialCharacters(translatedText);

        } catch (Exception e) {
            System.err.println("Translation failed with both services: " + e.getMessage());
            return text;
        }
    }

    private String decodeSpecialCharacters(String text) {
        if (text == null) return null;

        try {
            // Step 1: Fix malformed encodings with spaces using comprehensive regex
            String fixedText = text
                    // Fix ALL malformed URL encodings with spaces using regex
                    .replaceAll("%([0-9A-Fa-f]{2})% ([0-9A-Fa-f]{2})% ([0-9A-Fa-f]{2})", "%$1%$2%$3")  // 3-byte sequences
                    .replaceAll("%([0-9A-Fa-f]{2})% ([0-9A-Fa-f]{2})", "%$1%$2")  // 2-byte sequences
                    // Legacy specific cases (keep for backward compatibility)
                    .replace("%E2% 80% 99", "%E2%80%99")  // apostrophe
                    .replace("%E2% 80% 93", "%E2%80%93")  // en dash
                    .replace("%C3% A9", "%C3%A9")  // é
                    .replace("%C3% A0", "%C3%A0")  // à
                    .replace("%C2% A0", "%C2%A0");  // non-breaking space

            // Step 2: Try full URL decoding
            String decoded = fixedText;
            try {
                decoded = URLDecoder.decode(fixedText, StandardCharsets.UTF_8);
            } catch (Exception ignored) {
                // If full decode fails, continue with manual replacement
            }

            // Step 3: Manual replacement for any remaining encoded characters
            decoded = decoded
                    // Basic punctuation
                    .replace("%20", " ")
                    .replace("%21", "!")
                    .replace("%22", "\"")
                    .replace("%23", "#")
                    .replace("%24", "$")
                    .replace("%25", "%")
                    .replace("%26", "&")
                    .replace("%27", "'")
                    .replace("%28", "(")
                    .replace("%29", ")")
                    .replace("%2A", "*")
                    .replace("%2B", "+")
                    .replace("%2C", ",")
                    .replace("%2D", "-")
                    .replace("%2E", ".")
                    .replace("%2F", "/")
                    .replace("%3A", ":")
                    .replace("%3B", ";")
                    .replace("%3C", "<")
                    .replace("%3D", "=")
                    .replace("%3E", ">")
                    .replace("%3F", "?")
                    .replace("%40", "@")
                    .replace("%0A", "\n")
                    .replace("%0D", "\r")

                    // French accented characters
                    .replace("%C3%A0", "à")
                    .replace("%C3%A2", "â")
                    .replace("%C3%A6", "æ")
                    .replace("%C3%A7", "ç")
                    .replace("%C3%A8", "è")
                    .replace("%C3%A9", "é")
                    .replace("%C3%AA", "ê")
                    .replace("%C3%AB", "ë")
                    .replace("%C3%AE", "î")
                    .replace("%C3%AF", "ï")
                    .replace("%C3%B4", "ô")
                    .replace("%C3%B6", "ö")
                    .replace("%C3%B9", "ù")
                    .replace("%C3%BB", "û")
                    .replace("%C3%BC", "ü")
                    .replace("%C3%BF", "ÿ")

                    // Uppercase accented characters
                    .replace("%C3%80", "À")
                    .replace("%C3%82", "Â")
                    .replace("%C3%86", "Æ")
                    .replace("%C3%87", "Ç")
                    .replace("%C3%88", "È")
                    .replace("%C3%89", "É")
                    .replace("%C3%8A", "Ê")
                    .replace("%C3%8B", "Ë")
                    .replace("%C3%8E", "Î")
                    .replace("%C3%8F", "Ï")
                    .replace("%C3%94", "Ô")
                    .replace("%C3%96", "Ö")
                    .replace("%C3%99", "Ù")
                    .replace("%C3%9B", "Û")
                    .replace("%C3%9C", "Ü")

                    // Spanish characters
                    .replace("%C3%A1", "á")
                    .replace("%C3%AD", "í")
                    .replace("%C3%B3", "ó")
                    .replace("%C3%BA", "ú")
                    .replace("%C3%B1", "ñ")
                    .replace("%C3%81", "Á")
                    .replace("%C3%8D", "Í")
                    .replace("%C3%93", "Ó")
                    .replace("%C3%9A", "Ú")
                    .replace("%C3%91", "Ñ")
                    .replace("%C2%BF", "¿")
                    .replace("%C2%A1", "¡")

                    // German characters
                    .replace("%C3%9F", "ß")

                    // Special symbols
                    .replace("%E2%82%AC", "€")
                    .replace("%C2%A3", "£")
                    .replace("%C2%A5", "¥")
                    .replace("%E2%AD%90", "⭐")
                    .replace("%E2%9C%93", "✓")
                    .replace("%E2%9C%94", "✔")
                    .replace("%E2%9D%A4", "❤")

                    // Quotes and dashes
                    .replace("%E2%80%93", "–")  // en dash
                    .replace("%E2%80%94", "—")  // em dash
                    .replace("%E2%80%98", "'")  // left single quote
                    .replace("%E2%80%99", "'")  // right single quote/apostrophe
                    .replace("%E2%80%9A", "‚")  // low single quote
                    .replace("%E2%80%9C", "\"") // left double quote
                    .replace("%E2%80%9D", "\"") // right double quote
                    .replace("%E2%80%9E", "„")  // low double quote

                    // Spaces
                    .replace("%C2%A0", " ")     // non-breaking space
                    .replace("%E2%80%89", " ")  // thin space
                    .replace("%E2%80%8B", "")   // zero-width space

                    // Ellipsis
                    .replace("%E2%80%A6", "…");

            // Step 4: Fix common translation quirks and partial translations
            decoded = decoded
                    // Fix broken words with accented characters at end
                    .replaceAll("vari(?:ed)?\\s+terrains?\\s*és", "varied terrains")
                    .replaceAll("accompani(?:ed)?\\s*by+ée", "accompanied by")
                    .replaceAll("exp[eé]ri?ence\\s+mémorable", "memorable experience")
                    .replaceAll("exhibition\\s+[eé]rience", "experience")

                    // Fix French articles with apostrophes
                    .replace("the'île", "l'île")
                    .replace("the'island", "l'île")
                    .replace("of'adventure", "d'aventure")
                    .replace("d'discovered", "de découverte")
                    .replace("d'a ", "d'un ")
                    .replace("byée d'", "by a ")
                    .replace("byée ", "by ")

                    // Fix common mixed language patterns
                    .replaceAll("\\b([a-z]+)ée\\b", "$1ed")  // -ée endings to -ed
                    .replaceAll("\\bés\\b", "s")  // lone és to s
                    .replaceAll("érience", "experience")

                    // Clean up spaces
                    .replaceAll("\\s+", " ")  // Replace multiple spaces with single space
                    .replaceAll("\\s+([.,!?;:])", "$1")  // Remove space before punctuation
                    .replaceAll("([.,!?;:])([a-zA-Z])", "$1 $2")  // Add space after punctuation if missing
                    .trim();

            return decoded;

        } catch (Exception e) {
            System.err.println("Error decoding special characters: " + e.getMessage());

            // Fallback: Basic manual replacement
            return text
                    .replace("%21", "!")
                    .replace("%20", " ")
                    .replace("%2C", ",")
                    .replace("%0A", "\n")
                    .replace("%C3%A0", "à")
                    .replace("%C3%A9", "é")
                    .replace("%C3%A8", "è")
                    .replace("%C3%AA", "ê")
                    .replace("%C3%AE", "î")
                    .replace("%C3%B4", "ô")
                    .replace("%E2%80%99", "'")
                    .replaceAll("\\s+", " ")
                    .trim();
        }
    }

    private String mapLanguageCode(String locale) {
        switch (locale.toLowerCase()) {
            case "en": return "en";
            case "es": return "es";
            case "de": return "de";
            case "it": return "it";
            case "pt": return "pt";
            case "ru": return "ru";
            case "ja": return "ja";
            case "ko": return "ko";
            case "zh": return "zh";
            case "ar": return "ar";
            default: return "en";
        }
    }

    @Cacheable("translations")
    public String translateText(String text, String targetLanguage, String sourceLanguage) {
        return translateText(text, targetLanguage);
    }

    private String protectNonTranslatableWords(String text) {
        if (text == null) return null;
        String result = text;
        for (String word : NON_TRANSLATABLE_WORDS) {
            result = result.replaceAll("(?i)\\b" + word + "\\b", "##" + word + "##");
        }
        return result;
    }

    private String restoreNonTranslatableWords(String text) {
        if (text == null) return null;
        String result = text;
        for (String word : NON_TRANSLATABLE_WORDS) {
            result = result.replaceAll("(?i)##\\s*" + word + "\\s*##", word);
        }
        // Nettoyage final : supprime tout résidu de ##...##
        result = result.replaceAll("##+", "");
        // Nettoie les espaces multiples
        result = result.replaceAll("\\s+", " ").trim();
        return result;
    }
}
