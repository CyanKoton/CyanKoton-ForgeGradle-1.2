package net.minecraftforge.gradle.common;

import com.anatawa12.forge.gradle.separated.SeparatedLauncher;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import groovy.lang.Closure;
import io.github.cyankoton.gradle.Mirrorstation;
import net.minecraftforge.gradle.FileLogListenner;
import net.minecraftforge.gradle.GradleConfigurationException;
import net.minecraftforge.gradle.GradleVersionUtils;
import net.minecraftforge.gradle.ProjectUtils;
import net.minecraftforge.gradle.ThrowableUtils;
import net.minecraftforge.gradle.delayed.DelayedBase.IDelayedResolver;
import net.minecraftforge.gradle.delayed.DelayedFile;
import net.minecraftforge.gradle.delayed.DelayedFileTree;
import net.minecraftforge.gradle.delayed.DelayedString;
import net.minecraftforge.gradle.json.JsonFactory;
import net.minecraftforge.gradle.json.version.AssetIndex;
import net.minecraftforge.gradle.json.version.Version;
import net.minecraftforge.gradle.tasks.DownloadAssetsTask;
import net.minecraftforge.gradle.tasks.ExtractConfigTask;
import net.minecraftforge.gradle.tasks.ObtainFernFlowerTask;
import net.minecraftforge.gradle.tasks.abstractutil.DownloadTask;
import net.minecraftforge.gradle.tasks.abstractutil.EtagDownloadTask;
import org.gradle.api.*;
import org.gradle.api.artifacts.ArtifactRepositoryContainer;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.FlatDirectoryArtifactRepository;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Delete;
import org.gradle.testfixtures.ProjectBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BasePlugin<K extends BaseExtension> implements Plugin<Project>, IDelayedResolver<K> {
    public Project project;
    @SuppressWarnings("rawtypes")
    public BasePlugin otherPlugin;
    public Version version;
    public AssetIndex assetIndex;

    @SuppressWarnings("rawtypes")
    @Override
    public final void apply(Project arg) {
        project = arg;

        // search for overlays..
        for (Plugin p : project.getPlugins()) {
            if (p instanceof BasePlugin && p != this) {
                if (canOverlayPlugin()) {
                    project.getLogger().info("Applying Overlay");

                    // found another BasePlugin thats already applied.
                    // do only overlay stuff and return;
                    otherPlugin = (BasePlugin) p;
                    applyOverlayPlugin();
                    return;
                } else {
                    throw new GradleConfigurationException("Seems you are trying to apply 2 ForgeGradle plugins that are not designed to overlay... Fix your buildscripts.");
                }
            }
        }

        // Mirrorstation
        project.getExtensions().create(Mirrorstation.EXT_NAME_MIRRORSTATION, Mirrorstation.class, project);
        Constants.getMirrorstation(project);

        // logging
        {
            File projectCacheDir = project.getGradle().getStartParameter().getProjectCacheDir();
            if (projectCacheDir == null)
                projectCacheDir = new File(project.getProjectDir(), ".gradle");

            FileLogListenner listener = new FileLogListenner(new File(projectCacheDir, "gradle.log"));
            project.getLogging().addStandardOutputListener(listener);
            project.getLogging().addStandardErrorListener(listener);
            project.getGradle().addBuildListener(listener);
        }

        if (project.getBuildDir().getAbsolutePath().contains("!")) {
            project.getLogger().error("Build path has !, This will screw over a lot of java things as ! is used to denote archive paths, REMOVE IT if you want to continue");
            throw new RuntimeException("Build path contains !");
        }

        // extension objects
        project.getExtensions().create(Constants.EXT_NAME_MC, getExtensionClass(), this);
        project.getExtensions().create(Constants.EXT_NAME_JENKINS, JenkinsExtension.class, project);

        // repos
        project.allprojects(new Action<Project>() {
            public void execute(Project proj) {
                // the forge's repository doesn't have pom file.
                // addMavenRepo(proj, "forge", Constants.getMirrorstation().getForgeMavenUrl(), false);
                // proj.getRepositories().mavenCentral();
                // addMavenRepo(proj, "minecraft", Constants.getMirrorstation().getLibraryUrl());



                // addMavenRepo(proj, "aliyun", Constants.getMirrorstation().getAliyunMavenUrl());
                // addMavenRepo(proj, "mirror", Constants.getMirrorstation().getMirrorMavenUrl());
                // addMavenRepo(proj, "forge", Constants.getMirrorstation().getForgeMavenUrl(), false);
                // addMavenRepo(proj, "maven", Constants.getMirrorstation().getMavenMavenUrl());
                // proj.getRepositories().mavenCentral();
                // addMavenRepo(proj, "minecraft", Constants.getMirrorstation().getLibraryUrl());


                addMavenRepo(proj, "forge", Constants.getMirrorstation().getForgeMavenUrl(), false);
                addMavenRepo(proj, "aliyun", Constants.getMirrorstation().getAliyunMavenUrl());
                addMavenRepo(proj, "maven", Constants.getMirrorstation().getMavenMavenUrl());
                proj.getRepositories().mavenCentral();
                addMavenRepo(proj, "mirror", Constants.getMirrorstation().getMirrorMavenUrl());
                addMavenRepo(proj, "minecraft", Constants.getMirrorstation().getLibraryUrl());

            }
        });

        // do Mcp Snapshots Stuff
        project.getConfigurations().create(Constants.CONFIG_MCP_DATA);

        // Separated module
        /*
        {
            project.getConfigurations().create(SeparatedLauncher.configurationName);
            String version = getVersionString();
            if (version.indexOf('-') >= 0) {
                // remove git sha
                version = version.substring(0, version.lastIndexOf('-'));
                project.getDependencies().add(SeparatedLauncher.configurationName,
                        "com.anatawa12.forge:separated:" + version);
            }
        }
        */
        
        
        
        {
        project.getConfigurations().create(SeparatedLauncher.configurationName);
        String version = getVersionString();
        //String group = getGroupString();
            if (version.indexOf('-') >= 0) {
                // remove git sha
                version = version.substring(0, version.lastIndexOf('-'));
                project.getDependencies().add(SeparatedLauncher.configurationName, "io.github.cyankoton:separated:" + version);
            }
        }
        
        

        // after eval
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                // dont continue if its already failed!
                if (project.getState().getFailure() != null)
                    return;

                afterEvaluate();

                try {
                    if (version != null) {
                        File index = delayedFile(Constants.ASSETS + "/indexes/" + version.getAssets() + ".json").call();
                        if (index.exists())
                            parseAssetIndex();
                    }
                } catch (Exception e) {
                    ThrowableUtils.propagate(e);
                }

                finalCall();
            }
        });

        // some default tasks
        makeObtainTasks();

        // at last, apply the child plugins
        applyPlugin();
    }

    public abstract void applyPlugin();

    public abstract void applyOverlayPlugin();

    /**
     * return true if this plugin can be applied over another BasePlugin.
     *
     * @return TRUE if this can be applied upon another base plugin.
     */
    public abstract boolean canOverlayPlugin();

    protected abstract DelayedFile getDevJson();

    private static boolean displayBanner = true;

    public void afterEvaluate() {
        if (getExtension().mappingsSet()) {
            project.getDependencies().add(Constants.CONFIG_MCP_DATA, ImmutableMap.of(
                    "group", "de.oceanlabs.mcp",
                    "name", delayedString("mcp_{MAPPING_CHANNEL}").call(),
                    "version", delayedString("{MAPPING_VERSION}-{MC_VERSION}").call(),
                    "ext", "zip"
            ));
        }

        if (!displayBanner)
            return;
        Logger logger = this.project.getLogger();
        logger.lifecycle("###########################################################");
        logger.lifecycle("              ForgeGradle {}        ", this.getVersionString());
        // logger.lifecycle("   https://github.com/anatawa12/ForgeGradle-1.2            ");
        logger.lifecycle("   https://github.com/CyanKoton/CyanKoton-ForgeGradle-1.2  ");
        // logger.lifecycle("              anatawa12 ForgeGradle-1.2 for 1.7.10         ");
        logger.lifecycle("              Lss233 Reversions for 1.7.10                 ");
        logger.lifecycle("              beanflame CyanKoton ForgeGradle              ");
        logger.lifecycle("###########################################################");
        logger.lifecycle("              Powered by MCP {}               ", this.delayedString("{MCP_VERSION}"));
        // noinspection HttpUrlsUsage
        logger.lifecycle("              http://modcoderpack.com                      ");
        logger.lifecycle("              by: Searge, ProfMobius, Fesh0r,              ");
        logger.lifecycle("              R4wk, ZeuX, IngisKahn, bspkrs                ");
        logger.lifecycle("###########################################################");

        // 龙眼

        if (!hasMavenCentralBeforeJCenterInBuildScriptRepositories()) {
            logger.lifecycle("");
            logger.warn("The jcenter maven repository is going to be closed.");
            logger.warn("The fork of ForgeGradle by anatawa12 will use the maven central repository.");
            logger.warn("In the near future, this ForgeGradle will not be published onto the jcenter.");
            logger.warn("Please add the maven central repository to the repositories for");
            logger.warn("buildscript before or as a replacement of jcenter.");
        }
        if (!hasMavenMinecraftForgeBeforeFilesMinecraftForge(project.getBuildscript().getRepositories())
                || hasMavenMinecraftForgeBeforeFilesMinecraftForge(project.getRepositories())) {
            logger.lifecycle("");
            logger.warn("The minecraft forge's official maven repository has been moved to");
            logger.warn("https://maven.minecraftforge.net/. Currently redirection from previous location");
            logger.warn("previous location to new location is alive but we don't know");
            logger.warn("when it will stop so I especially recommend to change repository url.");
        }
        displayBanner = false;
    }

    private boolean hasMavenCentralBeforeJCenterInBuildScriptRepositories() {
        if (ProjectUtils.getBooleanProperty(project, "com.anatawa12.forge.gradle.no-maven-central-warn"))
            return true;
        java.net.URI mavenCentralUrl;
        try {
            mavenCentralUrl = project.uri(ArtifactRepositoryContainer.class
                    .getField("MAVEN_CENTRAL_URL").get(null));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        for (ArtifactRepository repository : project.getBuildscript().getRepositories()) {
            if (repository instanceof MavenArtifactRepository) {
                MavenArtifactRepository mvnRepo = (MavenArtifactRepository) repository;
                // requires before the jcenter
                if (mvnRepo.getUrl().toString().equals("https://jcenter.bintray.com/"))
                    return false;
                if (mvnRepo.getUrl().equals(mavenCentralUrl))
                    return true;
            }
        }
        return false;
    }

    private boolean hasMavenMinecraftForgeBeforeFilesMinecraftForge(RepositoryHandler repositories) {
        if (ProjectUtils.getBooleanProperty(project, "com.anatawa12.forge.gradle.no-forge-maven-warn"))
            return true;
        for (ArtifactRepository repository : repositories) {
            if (repository instanceof MavenArtifactRepository) {
                MavenArtifactRepository mvnRepo = (MavenArtifactRepository) repository;
                // requires before the jcenter
                if (mvnRepo.getUrl().toString().contains("//files.minecraftforge.net/maven"))
                    return false;
                if (mvnRepo.getUrl().toString().equals("https://maven.minecraftforge.net/"))
                    return true;
            }
        }
        return false;
    }

    private String getVersionString() {
        String version = this.getClass().getPackage().getImplementationVersion();
        if (Strings.isNullOrEmpty(version)) {
            version = "unknown version";
        }

        return version;
    }

    public void finalCall() {
    }

    @SuppressWarnings("serial")
    private void makeObtainTasks() {
        // download tasks
        DownloadTask task;

        task = makeTask("downloadClient", DownloadTask.class);
        {
            task.setOutput(delayedFile(Constants.JAR_CLIENT_FRESH));
            // task.setUrl(delayedString(Constants.MC_JAR_URL)); ok
            task.setUrl(delayedString(Constants.getMirrorstation().getMcJarUrl()));
        }

        task = makeTask("downloadServer", DownloadTask.class);
        {
            task.setOutput(delayedFile(Constants.JAR_SERVER_FRESH));
            // task.setUrl(delayedString(Constants.MC_SERVER_URL)); ok
            task.setUrl(delayedString(Constants.getMirrorstation().getMcServerUrl()));
        }

        ObtainFernFlowerTask mcpTask = makeTask("downloadMcpTools", ObtainFernFlowerTask.class);
        {
            mcpTask.setMcpUrl(delayedString(Constants.getMirrorstation().getMcpUrl()));
            // mcpTask.setMcpUrl(delayedString(Constants.MCP_URL)); ok
            mcpTask.setFfJar(delayedFile(Constants.FERNFLOWER));
        }

        EtagDownloadTask etagDlTask = makeTask("getAssetsIndex", EtagDownloadTask.class);
        {
            // etagDlTask.setUrl(delayedString(Constants.ASSETS_INDEX_URL)); ok
            etagDlTask.setUrl(delayedString(Constants.getMirrorstation().getAssetsIndexUrl()));
            etagDlTask.setFile(delayedFile(Constants.ASSETS + "/indexes/{ASSET_INDEX}.json"));
            etagDlTask.setDieWithError(false);

            etagDlTask.doLast(new Action<Task>() {
                public void execute(Task task) {
                    try {
                        parseAssetIndex();
                    } catch (Exception e) {
                        ThrowableUtils.propagate(e);
                    }
                }
            });
        }

        DownloadAssetsTask assets = makeTask("getAssets", DownloadAssetsTask.class);
        {
            assets.setAssetsDir(delayedFile(Constants.ASSETS));
            assets.setIndex(getAssetIndexClosure());
            assets.setIndexName(delayedString("{ASSET_INDEX}"));
            assets.dependsOn("getAssetsIndex");
        }

        etagDlTask = makeTask("getVersionJson", EtagDownloadTask.class);
        {
            // etagDlTask.setUrl(delayedString(Constants.MC_JSON_URL));
            etagDlTask.setUrl(delayedString(Constants.getMirrorstation().getMcJsonUrl()));
            etagDlTask.setFile(delayedFile(Constants.VERSION_JSON));
            etagDlTask.setDieWithError(false);
            etagDlTask.doLast(new Closure<Boolean>(project) // normalizes to linux endings
            {
                @Override
                public Boolean call() {
                    try {
                        File json = delayedFile(Constants.VERSION_JSON).call();
                        if (!json.exists())
                            return true;

                        List<String> lines = Files.readLines(json, Charsets.UTF_8);
                        StringBuilder buf = new StringBuilder();
                        for (String line : lines) {
                            buf = buf.append(line).append('\n');
                        }
                        Files.write(buf.toString().getBytes(Charsets.UTF_8), json);
                    } catch (Throwable t) {
                        ThrowableUtils.propagate(t);
                    }
                    return true;
                }
            });
        }

        Delete clearCache = makeTask("cleanCache", Delete.class);
        {
            clearCache.delete(delayedFile("{CACHE_DIR}/minecraft"));
            clearCache.setGroup("ForgeGradle");
            clearCache.setDescription("Cleares the ForgeGradle cache. DONT RUN THIS unless you want a fresh start, or the dev tells you to.");
        }

        // special userDev stuff
        ExtractConfigTask extractMcpData = makeTask("extractMcpData", ExtractConfigTask.class);
        {
            extractMcpData.setOut(delayedFile(Constants.MCP_DATA_DIR));
            extractMcpData.setConfig(Constants.CONFIG_MCP_DATA);
            extractMcpData.setDoesCache(true);
        }
    }

    public void parseAssetIndex() throws JsonSyntaxException, JsonIOException, IOException {
        assetIndex = JsonFactory.loadAssetsIndex(delayedFile(Constants.ASSETS + "/indexes/{ASSET_INDEX}.json").call());
    }

    @SuppressWarnings("serial")
    public Closure<AssetIndex> getAssetIndexClosure() {
        return new Closure<AssetIndex>(this, null) {
            public AssetIndex call(Object... obj) {
                return getAssetIndex();
            }
        };
    }

    public AssetIndex getAssetIndex() {
        return assetIndex;
    }

    /**
     * This extension object will have the name "minecraft"
     *
     * @return extension object class
     */
    @SuppressWarnings("unchecked")
    protected Class<K> getExtensionClass() {
        return (Class<K>) BaseExtension.class;
    }

    /**
     * @return the extension object with name
     * @see Constants#EXT_NAME_MC
     */
    @SuppressWarnings("unchecked")
    public final K getExtension() {
        if (otherPlugin != null && canOverlayPlugin())
            return getOverlayExtension();
        else
            return (K) project.getExtensions().getByName(Constants.EXT_NAME_MC);
    }

    /**
     * @return the extension object with name EXT_NAME_MC
     * @see Constants#EXT_NAME_MC
     */
    protected abstract K getOverlayExtension();

    public DefaultTask makeTask(String name) {
        return makeTask(name, DefaultTask.class);
    }

    public <T extends Task> T makeTask(String name, Class<T> type) {
        return makeTask(project, name, type);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Task> T makeTask(Project proj, String name, Class<T> type) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("name", name);
        map.put("type", type);
        return (T) proj.task(map, name);
    }

    public static Project getProject(File buildFile, Project parent) {
        ProjectBuilder builder = ProjectBuilder.builder();
        if (buildFile != null) {
            builder = builder.withProjectDir(buildFile.getParentFile())
                    .withName(buildFile.getParentFile().getName());
        } else {
            builder = builder.withProjectDir(new File("."));
        }

        if (parent != null) {
            builder = builder.withParent(parent);
        }

        Project project = builder.build();

        if (buildFile != null) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("from", buildFile.getAbsolutePath());

            project.apply(map);
        }

        return project;
    }

    public void applyExternalPlugin(String plugin) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("plugin", plugin);
        project.apply(map);
    }

    public MavenArtifactRepository addMavenRepo(Project proj, final String name, final String url) {
        return addMavenRepo(proj, name, url, true);
    }

    public MavenArtifactRepository addMavenRepo(final Project proj, final String name, final String url, final boolean usePom) {
        return proj.getRepositories().maven(new Action<MavenArtifactRepository>() {
            @Override
            public void execute(final MavenArtifactRepository repo) {
                repo.setName(name);
                repo.setUrl(url);
                if (!usePom) {
                    GradleVersionUtils.ifAfter("4.5", new Runnable() {
                        @Override
                        public void run() {
                            repo.metadataSources(new Action<MavenArtifactRepository.MetadataSources>() {
                                @Override
                                public void execute(MavenArtifactRepository.MetadataSources metadataSources) {
                                    metadataSources.artifact();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public FlatDirectoryArtifactRepository addFlatRepo(Project proj, final String name, final Object... dirs) {
        return proj.getRepositories().flatDir(new Action<FlatDirectoryArtifactRepository>() {
            @Override
            public void execute(FlatDirectoryArtifactRepository repo) {
                repo.setName(name);
                repo.dirs(dirs);
            }
        });
    }

    protected String getWithEtag(String strUrl, File cache, File etagFile) {
        try {
            if (project.getGradle().getStartParameter().isOffline()) // dont even try the internet
                return Files.asCharSource(cache, Charsets.UTF_8).read();

            // dude, its been less than 5 minutes since the last time..
            if (cache.exists() && cache.lastModified() + 300000 >= System.currentTimeMillis())
                return Files.asCharSource(cache, Charsets.UTF_8).read();

            String etag;
            if (etagFile.exists()) {
                etag = Files.asCharSource(etagFile, Charsets.UTF_8).read();
            } else {
                etagFile.getParentFile().mkdirs();
                etag = "";
            }

            URL url = new URL(strUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setInstanceFollowRedirects(true);
            con.setRequestProperty("User-Agent", Constants.USER_AGENT);

            if (!Strings.isNullOrEmpty(etag)) {
                con.setRequestProperty("If-None-Match", etag);
            }

            con.connect();

            String out = null;
            if (con.getResponseCode() == 304) {
                // the existing file is good
                Files.touch(cache); // touch it to update last-modified time
                out = Files.asCharSource(cache, Charsets.UTF_8).read();
            } else if (con.getResponseCode() == 200) {
                InputStream stream = con.getInputStream();
                byte[] data = ByteStreams.toByteArray(stream);
                Files.write(data, cache);
                stream.close();

                // write etag
                etag = con.getHeaderField("ETag");
                if (Strings.isNullOrEmpty(etag)) {
                    Files.touch(etagFile);
                } else {
                    Files.asCharSink(etagFile, Charsets.UTF_8).write(etag);
                }

                out = new String(data);
            } else {
                project.getLogger().error("Etag download for " + strUrl + " failed with code " + con.getResponseCode());
            }

            con.disconnect();

            return out;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cache.exists()) {
            try {
                return Files.asCharSource(cache, Charsets.UTF_8).read();
            } catch (IOException e) {
                ThrowableUtils.propagate(e);
            }
        }

        throw new RuntimeException("Unable to obtain url (" + strUrl + ") with etag!");
    }

    @Override
    public String resolve(String pattern, Project project, K exten) {
        if (version != null)
            pattern = pattern.replace("{ASSET_INDEX}", version.getAssets());

        if (exten.mappingsSet())
            pattern = pattern.replace("{MCP_DATA_DIR}", Constants.MCP_DATA_DIR);

        return pattern;
    }

    protected DelayedString delayedString(String path) {
        return new DelayedString(project, path, this);
    }

    protected DelayedFile delayedFile(String path) {
        return new DelayedFile(project, path, this);
    }

    protected DelayedFileTree delayedFileTree(String path) {
        return new DelayedFileTree(project, path, this);
    }

    protected DelayedFileTree delayedZipTree(String path) {
        return new DelayedFileTree(project, path, true, this);
    }

}
