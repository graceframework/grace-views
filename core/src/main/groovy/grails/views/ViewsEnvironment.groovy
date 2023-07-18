package grails.views

import grails.util.BuildSettings
import grails.util.Environment

/**
 * Environment helper methods
 *
 * @author Graeme Rocher
 */
class ViewsEnvironment {
    private static final boolean DEVELOPMENT_MODE = Environment.getCurrent() == Environment.DEVELOPMENT && BuildSettings.GRAILS_APP_DIR_PRESENT;

    /**
     * @return Whether development mode is enabled
     */
    static boolean isDevelopmentMode() {
        DEVELOPMENT_MODE
    }

    static String findTemplatePath() {
        File viewDir = new File(BuildSettings.GRAILS_APP_DIR, "views")
        return viewDir
    }

}
