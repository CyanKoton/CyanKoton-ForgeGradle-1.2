package io.github.cyankoton.gradle;

import org.gradle.api.Project;

// Mirrorstation
public class Mirrorstation {

    public static final String EXT_NAME_MIRRORSTATION = "mirrorstation";

    // Minecraft amd forge urls
    protected String mcJsonUrl       = "https://s3.amazonaws.com/Minecraft.Download/versions/{MC_VERSION}/{MC_VERSION}.json";
    protected String mcJarUrl        = "https://s3.amazonaws.com/Minecraft.Download/versions/{MC_VERSION}/{MC_VERSION}.jar";
    protected String mcServerUrl     = "https://s3.amazonaws.com/Minecraft.Download/versions/{MC_VERSION}/minecraft_server.{MC_VERSION}.jar";
    protected String mcpUrl          = "https://files.minecraftforge.net/fernflower-fix-1.0.zip";
    protected String assetsUrl       = "https://resources.download.minecraft.net";
    protected String libraryUrl      = "https://libraries.minecraft.net/";
    protected String assetsIndexUrl  = "https://s3.amazonaws.com/Minecraft.Download/indexes/{ASSET_INDEX}.json";
    protected String forgeMavenUrl   = "https://maven.minecraftforge.net";

    // mirror maven amd MavenMaven urls
    protected String mirrorMavenUrl  = "https://cyankoton.github.io/maven";
    protected String aliyunMavenUrl  = "https://maven.aliyun.com/repository/public";
    protected String mavenMavenUrl   = "https://repo1.maven.org/maven2";



    // ---------------------------------------------------------------------------------------------------------------

    /*
    public static Mirrorstation getInstance(Project project) {
        return project.getExtensions().create(EXT_NAME_MIRRORSTATION, Mirrorstation.class);
    }
    */

    // public Mirrorstation(Project project) { System.out.println("project = " + project); }

    public Mirrorstation(Project project) {
        // System.out.println("project = " + project);
        System.out.println("Mirrorstation - Version : 0.1.0");
    }

    // mcJsonUrl  ok
    public String getMcJsonUrl() { return mcJsonUrl; }
    public void setMcJsonUrl(String mcJsonUrl) { this.mcJsonUrl = mcJsonUrl; }

    // MC_JAR_URL   ok
    public String getMcJarUrl() { return mcJarUrl; }
    public void setMcJarUrl(String mcJarUrl) { this.mcJarUrl = mcJarUrl; }

    // MC_SERVER_URL    ok
    public String getMcServerUrl() { return mcServerUrl; }
    public void setMcServerUrl(String mcServerUrl) { this.mcServerUrl = mcServerUrl; }

    // MCP_URL  ok
    public String getMcpUrl() { return mcpUrl; }
    public void setMcpUrl(String mcpUrl) { this.mcpUrl = mcpUrl; }

    // ASSETS_URL   ok
    public String getAssetsUrl() { return assetsUrl; }
    public void setAssetsUrl(String assetsUrl) { this.assetsUrl = assetsUrl; }

    // LIBRARY_URL  ok
    public String getLibraryUrl() { return libraryUrl; }
    public void setLibraryUrl(String libraryUrl) { this.libraryUrl = libraryUrl; }

    // ASSETS_INDEX_URL ok
    public String getAssetsIndexUrl() { return assetsIndexUrl; }
    public void setAssetsIndexUrl(String assetsIndexUrl) { this.assetsIndexUrl = assetsIndexUrl; }


    // -----------------------------------------------------------------------------------------------

    // MIRROR_MAVEN_URL ok
    public String getMirrorMavenUrl() { return mirrorMavenUrl; }
    public void setMirrorMavenUrl(String mirrorMavenUrl) { this.mirrorMavenUrl = mirrorMavenUrl; }

    // ALIYUN_MAVEN_URL ok
    public String getAliyunMavenUrl() { return aliyunMavenUrl; }
    public void setAliyunMavenUrl(String aliyunMavenUrl) { this.aliyunMavenUrl = aliyunMavenUrl; }

    // MAVEN_MAVEN_URL  ok
    public String getMavenMavenUrl() { return mavenMavenUrl; }
    public void setMavenMavenUrl(String mavenMavenUrl) { this.mavenMavenUrl = mavenMavenUrl; }

    // FORGE_MAVEN_URL  ok
    public String getForgeMavenUrl() { return forgeMavenUrl; }
    public void setForgeMavenUrl(String forgeMavenUrl) { this.forgeMavenUrl = forgeMavenUrl; }


}
