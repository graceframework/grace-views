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
        def current = Environment.getCurrent()
        def pathToTemplates = current.hasReloadLocation() ? current.getReloadLocation() : BuildSettings.BASE_DIR.absolutePath
        if (pathToTemplates) {
            for (String appDir in ['grails-app', 'app']) {
                File viewDir = new File(pathToTemplates, "$appDir/views")
                if (viewDir.exists()) {
                    return viewDir.absolutePath
                }
            }
        }
        return null
    }

}
