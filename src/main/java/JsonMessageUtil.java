import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JsonMessageUtil {

    public String getJSONString(String input) {
        if(input == null) return "{}";

        List<String> tokens = Arrays.asList(input.split(" "));

        List<String> mentions = tokens.parallelStream()
                                      .filter(e -> e.startsWith("@") && e.length() > 1)
                                      .map(e -> e.substring(1))
                                      .collect(Collectors.toList());

        List<String> emoticons = tokens.parallelStream()
                                       .filter(e -> e.startsWith("(") && e.endsWith(")") && e.length() > 2)
                                       .map(e -> e.substring(1, e.length() - 1))
                                       .collect(Collectors.toList());

        List<Link> links = tokens.parallelStream()
                                 .filter(e -> (e.startsWith("http://") && e.length() > 7) || (e.startsWith("https://") && e.length() > 8))
                                 .map(e -> new Link(e, getTitle(e)))
                                 .collect(Collectors.toList());

        return getJSONString(mentions, emoticons, links);
    }

    private String getJSONString(List<String> mentions, List<String> emoticons, List<Link> links) {
        StringBuilder message = new StringBuilder();
        message.append("{");

        if(!mentions.isEmpty()) {
            message.append("\"mentions\":[").append(convertListToJSON(mentions)).append("]");
        }

        if(!emoticons.isEmpty()) {
            if(message.length() > 2) message.append(",");
            message.append("\"emoticons\":[").append(convertListToJSON(emoticons)).append("]");
        }
        if(!links.isEmpty()) {
            if(message.length() > 2) message.append(",");
            message.append("\"links\":[").append(convertListToJSON(links)).append("]");
        }
        return message + "}";
    }

    private String convertListToJSON(List<?> values) {
        return values.parallelStream().map(JsonMessageUtil::getJSON).collect(Collectors.joining(","));
    }

    private static String getJSON(Object object) {
        if(object instanceof String)
            return "\"" + object + "\"";
        return object.toString();
    }

    class Link {
        String url;
        String title;

        Link(String url, String title) {
            this.url = url;
            this.title = title;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{\"url\":");
            sb.append("\"").append(url).append("\"");
            sb.append(",\"title\":");
            sb.append("\"").append(title).append("\"");
            sb.append("}");
            return sb.toString();
        }
    }

    private static final Pattern TITLE_TAG = Pattern.compile("\\<title>(.*)\\</title>", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);

    private String getTitle(String url) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()));
            int n, totalRead = 0;
            char[] buf = new char[1024];
            StringBuilder content = new StringBuilder();

            while (totalRead < 8192 && (n = reader.read(buf, 0, buf.length)) != -1) {
                content.append(buf, 0, n);
                totalRead += n;
            }

            Matcher matcher = TITLE_TAG.matcher(content);
            if (matcher.find()) {
                return matcher.group(1).replaceAll("[\\s\\<>]+", " ").trim();
            }
        } catch (Exception e) {
            return "";
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
        return "";
    }
}
