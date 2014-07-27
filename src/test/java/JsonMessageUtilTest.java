import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class JsonMessageUtilTest {
    JsonMessageUtil jsonMessageUtil = new JsonMessageUtil();

    @Test
    public void testEmpty() {
        assertEquals("{}", jsonMessageUtil.getJSONString(""));
    }

    @Test
    public void testNull() {
        assertEquals("{}", jsonMessageUtil.getJSONString(null));
    }

    @Test
    public void testJustMessage() {
        assertEquals("{}", jsonMessageUtil.getJSONString("Hello World!!!"));
    }

    @Test
    public void testOnlyAtSymbols() {
        assertEquals("{}", jsonMessageUtil.getJSONString("@ @ @ @"));
    }

    @Test
    public void testGetJSONStringOnlyMentions() {
        assertEquals("{\"mentions\":[\"chris\"]}", jsonMessageUtil.getJSONString("@chris you around?"));
    }

    @Test
    public void testOnlyParenthesis() {
        assertEquals("{}", jsonMessageUtil.getJSONString("() () () ()"));
    }

    @Test
    public void testGetJSONStringOnlyEmoticons() {
        assertEquals("{\"emoticons\":[\"megusta\",\"coffee\"]}", jsonMessageUtil.getJSONString("Good morning! (megusta) (coffee)"));
    }

    @Test
    public void testGetJSONStringEmptyUrls() {
        assertEquals("{}", jsonMessageUtil.getJSONString("Olympics are starting soon; http://"));
    }

    @Test
    public void testGetJSONStringEmptyUrlsWithHttps() {
        assertEquals("{}", jsonMessageUtil.getJSONString("Olympics are starting soon; https://"));
    }

    @Test
    public void testGetJSONStringOnlyUrls() {
        assertEquals("{\"links\":[{\"url\":\"http://www.nbcolympics.com\",\"title\":\"| NBC Olympics\"}]}", jsonMessageUtil.getJSONString("Olympics are starting soon; http://www.nbcolympics.com"));
    }

    @Test
    public void testAll() {
        assertEquals("{\"mentions\":[\"bob\",\"john\"],\"emoticons\":[\"success\"],\"links\":[{\"url\":\"https://twitter.com/jdorfman/status/430511497475670016\",\"title\":\"Twitter / jdorfman: nice @littlebigdetail from ...\"}]}",
                jsonMessageUtil.getJSONString("@bob @john (success) such a cool feature; https://twitter.com/jdorfman/status/430511497475670016"));
    }
}
