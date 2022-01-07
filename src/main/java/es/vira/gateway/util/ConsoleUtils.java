package es.vira.gateway.util;

import es.vira.gateway.Application;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * console utils
 *
 * @author Víctor Gómez
 * @since 2.2.1
 */
public class ConsoleUtils {
    /**
     * get version
     *
     * @return version
     * @throws IOException read manifest error
     */
    public static String getVersion() throws IOException {
        JarFile jarFile = new JarFile(Application.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        Manifest manifest = jarFile.getManifest();
        Attributes attribute = manifest.getMainAttributes();
        return attribute.getValue("Version");
    }
}
