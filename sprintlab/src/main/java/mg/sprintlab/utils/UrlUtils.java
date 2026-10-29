package mg.sprintlab.utils;

/**
 * One single way to write a URL, used both when registering and when looking up.
 */
public final class UrlUtils {

    private UrlUtils() {
    }

    /** "product/list/" -> "/product/list" ; "" -> "/" */
   public static String normalize(String url) {
        if (url == null) {
            return "/";
        }
        String result = url.trim();
        if (!result.startsWith("/")) {
            result = "/" + result;
        }
        if (result.length() > 1 && result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
