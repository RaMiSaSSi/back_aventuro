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
import java.text.Normalizer;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.springframework.context.MessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Locale;

@Service
public class TranslationService {
    @Autowired
    private MessageSource messageSource;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SpecialCharacterDecoder specialCharDecoder = new SpecialCharacterDecoder();

    private static final java.util.Set<String> NON_TRANSLATABLE_WORDS = new java.util.HashSet<>(java.util.Arrays.asList(
            "Djerba", "Chenini", "Tataouine", "Sahara", "Prestige", "Pirate"
    ));

    /**
     * Récupère la traduction d'une clé à partir des fichiers de ressources
     */
    public String getTranslationByKey(String key, String lang) {
        if (key == null || key.trim().isEmpty()) return "";
        Locale locale = Locale.forLanguageTag(lang);
        try {
            String translation = messageSource.getMessage(key, null, locale);
            return translation != null ? translation : key;
        } catch (Exception e) {
            // Si la clé n'existe pas, retourner la clé elle-même
            return key;
        }
    }

    @Cacheable("translations")
    public String translateText(String text, String targetLanguage) {
        if (text == null || text.trim().isEmpty()) return text;
        // Vérifier si le texte est une clé de traduction (ex: contient un point)
        if (text.contains(".")) {
            String translation = getTranslationByKey(text, targetLanguage);
            if (translation != null && !translation.equals(text)) {
                return translation;
            }
        }

        if (text.trim().equalsIgnoreCase("Croisière Pirate Exclusive") && targetLanguage.equalsIgnoreCase("en")) {
            System.out.println("[TranslationService] Manual translation for: " + text);
            return "Exclusive Pirate Cruise";
        }
        String cleanedText = cleanSourceText(text);
        String protectedText = protectNonTranslatableWords(cleanedText);
        try {
            String detectedSourceLang = detectLanguage(protectedText);
            if (detectedSourceLang == null) detectedSourceLang = "fr";
            String url = "https://libretranslate.de/translate";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String escapedText = escapeJsonString(protectedText);
            String requestBody = String.format(
                    "{\"q\":\"%s\",\"source\":\"%s\",\"target\":\"%s\"}",
                    escapedText,
                    mapLanguageCode(detectedSourceLang),
                    mapLanguageCode(targetLanguage)
            );
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
            JsonNode jsonNode = objectMapper.readTree(response);
            String translatedText = jsonNode.get("translatedText").asText();
            String finalText = restoreNonTranslatableWords(specialCharDecoder.decodeAll(translatedText));
            finalText = postProcessTranslation(finalText);
            System.out.println("[TranslationService] LibreTranslate input: " + text + " | output: " + finalText);
            if (finalText.trim().equalsIgnoreCase(text.trim())) {
                System.out.println("[TranslationService] Fallback to MyMemory API for: " + text);
                String fallback = restoreNonTranslatableWords(translateWithMyMemory(protectedText, targetLanguage));
                fallback = postProcessTranslation(fallback);
                System.out.println("[TranslationService] MyMemory output: " + fallback);
                if (fallback.trim().equalsIgnoreCase(text.trim()) &&
                        text.trim().equalsIgnoreCase("Croisière Pirate Exclusive") &&
                        targetLanguage.equalsIgnoreCase("en")) {
                    return "Exclusive Pirate Cruise";
                }
                return fallback;
            }
            return finalText;
        } catch (Exception e) {
            System.err.println("LibreTranslate failed, trying MyMemory API: " + e.getMessage());
            String fallback = restoreNonTranslatableWords(translateWithMyMemory(protectedText, targetLanguage));
            fallback = postProcessTranslation(fallback);
            return fallback;
        }
    }

    private String escapeJsonString(String text) {
        if (text == null) return null;
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\f", "\\f");
    }

    private String detectLanguage(String text) {
        try {
            String url = "https://libretranslate.de/detect";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String requestBody = String.format("{\"q\":\"%s\"}", text.replace("\"", "\\\""));
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            String response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
            JsonNode jsonNode = objectMapper.readTree(response);
            if (jsonNode.isArray() && !jsonNode.isEmpty()) {
                return jsonNode.get(0).get("language").asText();
            }
        } catch (Exception e) {
            System.err.println("Language detection failed: " + e.getMessage());
        }
        return null;
    }

    private String cleanSourceText(String text) {
        if (text == null) return null;
        return text
                .replaceAll("–", "-")
                .replaceAll("—", "-")
                .replaceAll("’", "'")
                .replaceAll("‘", "'")
                .replaceAll("\"", "\"")
                .replaceAll("…", "...")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String postProcessTranslation(String text) {
        if (text == null) return null;
        String result = text;
        result = result
                .replace("à through", "through")
                .replace("expexperience", "experience")
                .replace("arrêts", "stops")
                .replace("dédiscovered", "discovery")
                .replace("l'île", "the island")
                .replace("l'Île", "the Island")
                .replace("idéal", "ideal")
                .replace("d'aventure", "of adventure")
                .replace("découverte", "discovery")
                .replace("Décovered", "Discover")
                .replace("expérience", "experience")
                .replace("agréable", "pleasant")
                .replace("immersive", "an immersive")
                .replace("several arrêts", "several stops")
                // Suppression générale des accents français résiduels en anglais
                .replace("é", "e")
                .replace("è", "e")
                .replace("ê", "e")
                .replace("à", "a")
                .replace("â", "a")
                .replace("ô", "o")
                .replace("î", "i")
                .replace("û", "u")
                .replace("ù", "u")
                .replace("ç", "c");
        result = result.replaceAll("\\s+", " ").trim();
        return result;
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

            return specialCharDecoder.decodeAll(translatedText);

        } catch (Exception e) {
            System.err.println("Translation failed with both services: " + e.getMessage());
            return text;
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
        // sourceLanguage n'est pas utilisé, conservé pour compatibilité
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
        result = result.replaceAll("##+", "");
        result = result.replaceAll("\\s+", " ").trim();
        return result;
    }

    /**
     * Classe interne pour gérer le décodage des caractères spéciaux
     */
    private static class SpecialCharacterDecoder {
        private final Map<String, String> urlEncodedChars;
        private final Map<String, String> htmlEntities;
        private final Pattern htmlEntityPattern;
        private final Pattern unicodePattern;

        public SpecialCharacterDecoder() {
            this.urlEncodedChars = initUrlEncodedChars();
            this.htmlEntities = initHtmlEntities();
            this.htmlEntityPattern = Pattern.compile("&[a-zA-Z0-9]+;|&#[0-9]+;|&#x[0-9A-Fa-f]+;");
            this.unicodePattern = Pattern.compile("\\\\u[0-9A-Fa-f]{4}");
        }

        public String decodeAll(String text) {
            if (text == null || text.trim().isEmpty()) return text;

            try {
                String decoded = text;

                // 1. Réparer encodages malformés
                decoded = fixMalformedUrlEncoding(decoded);

                // 2. Décodage URL complet
                try {
                    decoded = URLDecoder.decode(decoded, StandardCharsets.UTF_8);
                } catch (Exception e) {
                    // Continue avec remplacement manuel
                }

                // 3. Remplacements manuels URL
                decoded = decodeUrlEncoded(decoded);

                // 4. HTML entities
                decoded = decodeHtmlEntities(decoded);

                // 5. Unicode escapes
                decoded = decodeUnicodeEscapes(decoded);

                // 6. Normalisation
                decoded = normalizeUnicode(decoded);

                // 7. Post-traitement
                decoded = postProcessDecoded(decoded);

                return decoded;

            } catch (Exception e) {
                System.err.println("Error decoding special characters: " + e.getMessage());
                return basicFallbackDecode(text);
            }
        }

        private String fixMalformedUrlEncoding(String text) {
            // Réparer %XX% XX% XX en %XX%XX%XX
            text = text.replaceAll("%([0-9A-Fa-f]{2})%\\s*([0-9A-Fa-f]{2})%\\s*([0-9A-Fa-f]{2})", "%$1%$2%$3");
            text = text.replaceAll("%([0-9A-Fa-f]{2})%\\s*([0-9A-Fa-f]{2})", "%$1%$2");
            return text;
        }

        private String decodeUrlEncoded(String text) {
            for (Map.Entry<String, String> entry : urlEncodedChars.entrySet()) {
                text = text.replace(entry.getKey().toUpperCase(), entry.getValue());
                text = text.replace(entry.getKey().toLowerCase(), entry.getValue());
            }
            return text;
        }

        // Suppression du champ urlEncodedPattern inutilisé
        // Remplacement de StringBuffer par StringBuilder
        private String decodeHtmlEntities(String text) {
            Matcher matcher = htmlEntityPattern.matcher(text);
            StringBuilder sb = new StringBuilder();
            while (matcher.find()) {
                String entity = matcher.group();
                String replacement = htmlEntities.getOrDefault(entity.toLowerCase(), entity);
                if (entity.startsWith("&#x")) {
                    try {
                        int codePoint = Integer.parseInt(entity.substring(3, entity.length() - 1), 16);
                        replacement = String.valueOf((char) codePoint);
                    } catch (Exception e) {
                        replacement = entity;
                    }
                } else if (entity.startsWith("&#")) {
                    try {
                        int codePoint = Integer.parseInt(entity.substring(2, entity.length() - 1));
                        replacement = String.valueOf((char) codePoint);
                    } catch (Exception e) {
                        replacement = entity;
                    }
                }
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(sb);
            return sb.toString();
        }
        private String decodeUnicodeEscapes(String text) {
            Matcher matcher = unicodePattern.matcher(text);
            StringBuilder sb = new StringBuilder();
            while (matcher.find()) {
                String unicode = matcher.group();
                try {
                    int codePoint = Integer.parseInt(unicode.substring(2), 16);
                    String replacement = String.valueOf((char) codePoint);
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
                } catch (Exception e) {
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(unicode));
                }
            }
            matcher.appendTail(sb);
            return sb.toString();
        }

        private String normalizeUnicode(String text) {
            // Normalise les caractères composés
            return Normalizer.normalize(text, Normalizer.Form.NFC);
        }

        private String postProcessDecoded(String text) {
            return text
                    .replaceAll("\\b([a-z]+)ée\\b", "$1ed")
                    .replaceAll("\\bés\\b", "s")
                    .replaceAll("\\s+", " ")
                    .replaceAll("\\s+([.,!?;:])", "$1")
                    .replaceAll("([.,!?;:])([a-zA-Z])", "$1 $2")
                    .trim();
        }

        private String basicFallbackDecode(String text) {
            return text
                    .replace("%20", " ")
                    .replace("%C3%A0", "à")
                    .replace("%C3%A9", "é")
                    .replace("%C3%A8", "è")
                    .replace("%C3%AA", "ê")
                    .replace("%E2%80%99", "'")
                    .replace("&nbsp;", " ")
                    .replace("&eacute;", "é")
                    .replace("&agrave;", "à")
                    .replaceAll("\\s+", " ")
                    .trim();
        }

        private Map<String, String> initUrlEncodedChars() {
            Map<String, String> map = new HashMap<>();
            // Ponctuation basique
            map.put("%20", " "); map.put("%21", "!"); map.put("%22", "\"");
            map.put("%23", "#"); map.put("%24", "$"); map.put("%25", "%");
            map.put("%26", "&"); map.put("%27", "'"); map.put("%28", "(");
            map.put("%29", ")"); map.put("%2A", "*"); map.put("%2B", "+");
            map.put("%2C", ","); map.put("%2D", "-"); map.put("%2E", ".");
            map.put("%2F", "/"); map.put("%3A", ":"); map.put("%3B", ";");
            map.put("%3C", "<"); map.put("%3D", "="); map.put("%3E", ">");
            map.put("%3F", "?"); map.put("%40", "@"); map.put("%5B", "[");
            map.put("%5C", "\\"); map.put("%5D", "]"); map.put("%5E", "^");
            map.put("%5F", "_"); map.put("%60", "`"); map.put("%7B", "{");
            map.put("%7C", "|"); map.put("%7D", "}"); map.put("%7E", "~");
            map.put("%0A", "\n"); map.put("%0D", "\r"); map.put("%09", "\t");

            // Caractères accentués minuscules
            map.put("%C3%A0", "à"); map.put("%C3%A1", "á"); map.put("%C3%A2", "â");
            map.put("%C3%A3", "ã"); map.put("%C3%A4", "ä"); map.put("%C3%A5", "å");
            map.put("%C3%A6", "æ"); map.put("%C3%A7", "ç"); map.put("%C3%A8", "è");
            map.put("%C3%A9", "é"); map.put("%C3%AA", "ê"); map.put("%C3%AB", "ë");
            map.put("%C3%AC", "ì"); map.put("%C3%AD", "í"); map.put("%C3%AE", "î");
            map.put("%C3%AF", "ï"); map.put("%C3%B0", "ð"); map.put("%C3%B1", "ñ");
            map.put("%C3%B2", "ò"); map.put("%C3%B3", "ó"); map.put("%C3%B4", "ô");
            map.put("%C3%B5", "õ"); map.put("%C3%B6", "ö"); map.put("%C3%B8", "ø");
            map.put("%C3%B9", "ù"); map.put("%C3%BA", "ú"); map.put("%C3%BB", "û");
            map.put("%C3%BC", "ü"); map.put("%C3%BD", "ý"); map.put("%C3%BE", "þ");
            map.put("%C3%BF", "ÿ");

            // Majuscules accentuées
            map.put("%C3%80", "À"); map.put("%C3%81", "Á"); map.put("%C3%82", "Â");
            map.put("%C3%83", "Ã"); map.put("%C3%84", "Ä"); map.put("%C3%85", "Å");
            map.put("%C3%86", "Æ"); map.put("%C3%87", "Ç"); map.put("%C3%88", "È");
            map.put("%C3%89", "É"); map.put("%C3%8A", "Ê"); map.put("%C3%8B", "Ë");
            map.put("%C3%8C", "Ì"); map.put("%C3%8D", "Í"); map.put("%C3%8E", "Î");
            map.put("%C3%8F", "Ï"); map.put("%C3%90", "Ð"); map.put("%C3%91", "Ñ");
            map.put("%C3%92", "Ò"); map.put("%C3%93", "Ó"); map.put("%C3%94", "Ô");
            map.put("%C3%95", "Õ"); map.put("%C3%96", "Ö"); map.put("%C3%98", "Ø");
            map.put("%C3%99", "Ù"); map.put("%C3%9A", "Ú"); map.put("%C3%9B", "Û");
            map.put("%C3%9C", "Ü"); map.put("%C3%9D", "Ý"); map.put("%C3%9E", "Þ");
            map.put("%C3%9F", "ß");

            // Symboles et devises
            map.put("%E2%82%AC", "€"); map.put("%C2%A3", "£"); map.put("%C2%A5", "¥");
            map.put("%C2%A9", "©"); map.put("%C2%AE", "®"); map.put("%E2%84%A2", "™");
            map.put("%C2%B0", "°"); map.put("%C2%B1", "±"); map.put("%C2%BC", "¼");
            map.put("%C2%BD", "½"); map.put("%C2%BE", "¾"); map.put("%C2%BF", "¿");
            map.put("%C2%A1", "¡"); map.put("%C2%AB", "«"); map.put("%C2%BB", "»");

            // Guillemets et tirets
            map.put("%E2%80%93", "–"); map.put("%E2%80%94", "—");
            map.put("%E2%80%98", "'"); map.put("%E2%80%99", "'");
            map.put("%E2%80%9C", "\""); map.put("%E2%80%9D", "\"");
            map.put("%E2%80%A6", "…"); map.put("%E2%80%A2", "•");

            // Espaces
            map.put("%C2%A0", " "); map.put("%E2%80%89", " ");
            map.put("%E2%80%8B", ""); map.put("%E2%80%8C", "");

            // Symboles math
            map.put("%C3%97", "×"); map.put("%C3%B7", "÷");
            map.put("%E2%88%92", "−"); map.put("%E2%88%9E", "∞");

            // Emojis courants
            map.put("%E2%AD%90", "⭐"); map.put("%E2%9C%93", "✓");
            map.put("%E2%9C%94", "✔"); map.put("%E2%9D%A4", "❤");

            return map;
        }

        private Map<String, String> initHtmlEntities() {
            Map<String, String> map = new HashMap<>();
            map.put("&nbsp;", " "); map.put("&lt;", "<"); map.put("&gt;", ">");
            map.put("&amp;", "&"); map.put("&quot;", "\""); map.put("&apos;", "'");
            map.put("&euro;", "€"); map.put("&pound;", "£"); map.put("&yen;", "¥");
            map.put("&copy;", "©"); map.put("&reg;", "®"); map.put("&trade;", "™");
            map.put("&agrave;", "à"); map.put("&aacute;", "á"); map.put("&acirc;", "â");
            map.put("&atilde;", "ã"); map.put("&auml;", "ä"); map.put("&aring;", "å");
            map.put("&aelig;", "æ"); map.put("&ccedil;", "ç"); map.put("&egrave;", "è");
            map.put("&eacute;", "é"); map.put("&ecirc;", "ê"); map.put("&euml;", "ë");
            map.put("&igrave;", "ì"); map.put("&iacute;", "í"); map.put("&icirc;", "î");
            map.put("&iuml;", "ï"); map.put("&ntilde;", "ñ"); map.put("&ograve;", "ò");
            map.put("&oacute;", "ó"); map.put("&ocirc;", "ô"); map.put("&otilde;", "õ");
            map.put("&ouml;", "ö"); map.put("&oslash;", "ø"); map.put("&ugrave;", "ù");
            map.put("&uacute;", "ú"); map.put("&ucirc;", "û"); map.put("&uuml;", "ü");
            map.put("&yacute;", "ý"); map.put("&yuml;", "ÿ");
            map.put("&Agrave;", "À"); map.put("&Aacute;", "Á"); map.put("&Acirc;", "Â");
            map.put("&Atilde;", "Ã"); map.put("&Auml;", "Ä"); map.put("&Aring;", "Å");
            map.put("&AElig;", "Æ"); map.put("&Ccedil;", "Ç"); map.put("&Egrave;", "È");
            map.put("&Eacute;", "É"); map.put("&Ecirc;", "Ê"); map.put("&Euml;", "Ë");
            map.put("&Igrave;", "Ì"); map.put("&Iacute;", "Í"); map.put("&Icirc;", "Î");
            map.put("&Iuml;", "Ï"); map.put("&Ntilde;", "Ñ"); map.put("&Ograve;", "Ò");
            map.put("&Oacute;", "Ó"); map.put("&Ocirc;", "Ô"); map.put("&Otilde;", "Õ");
            map.put("&Ouml;", "Ö"); map.put("&Oslash;", "Ø"); map.put("&Ugrave;", "Ù");
            map.put("&Uacute;", "Ú"); map.put("&Ucirc;", "Û"); map.put("&Uuml;", "Ü");
            map.put("&Yacute;", "Ý"); map.put("&szlig;", "ß");
            map.put("&mdash;", "—"); map.put("&ndash;", "–");
            map.put("&lsquo;", "'"); map.put("&rsquo;", "'");
            map.put("&ldquo;", "\""); map.put("&rdquo;", "\"");
            map.put("&hellip;", "…"); map.put("&bull;", "•");
            map.put("&times;", "×"); map.put("&divide;", "÷");
            map.put("&deg;", "°"); map.put("&plusmn;", "±");
            map.put("&frac14;", "¼"); map.put("&frac12;", "½"); map.put("&frac34;", "¾");
            return map;
        }
    }
}

