package me.liwenkun.buildSrc;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class MyPlugin implements Plugin<Project> {


    private static final class MyTransformer extends Transform {

        @Override
        public String getName() {
            return "me.liwenkun";
        }

        @Override
        public Set<QualifiedContent.ContentType> getInputTypes() {
            return Collections.singleton(QualifiedContent.DefaultContentType.CLASSES);
        }

        @Override
        public Set<? super QualifiedContent.Scope> getScopes() {
            return Collections.singleton(QualifiedContent.Scope.PROJECT);
        }

        @Override
        public boolean isIncremental() {
            return true;
        }

        @Override
        public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
            TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
            transformInvocation.getInputs().stream().flatMap(new Function<TransformInput, Stream<QualifiedContent>>() {
                @Override
                public Stream<QualifiedContent> apply(TransformInput transformInput) {
                    return Stream.concat(transformInput.getJarInputs().stream(), transformInput.getDirectoryInputs().stream());
                }
            }).forEach(new Consumer<QualifiedContent>() {
                @Override
                public void accept(QualifiedContent jarInput) {
                    if (jarInput instanceof JarInput) {
                        File dest = outputProvider.getContentLocation(
                                jarInput.getFile().getAbsolutePath(),
                                jarInput.getContentTypes(),
                                jarInput.getScopes(),
                                Format.JAR);
                        try {
                            FileUtils.copyFile(jarInput.getFile(), dest);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (jarInput instanceof DirectoryInput) {
                        File dest = outputProvider.getContentLocation(jarInput.getName(),
                                jarInput.getContentTypes(), jarInput.getScopes(),
                                Format.DIRECTORY);
                        try {
                            FileUtils.copyFile(jarInput.getFile(), dest);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private static final class MyTransformer2 extends Transform {

        @Override
        public String getName() {
            return "me";
        }

        @Override
        public Set<QualifiedContent.ContentType> getInputTypes() {
            return Collections.singleton(QualifiedContent.DefaultContentType.CLASSES);
        }

        @Override
        public Set<? super QualifiedContent.Scope> getScopes() {
            return Collections.singleton(QualifiedContent.Scope.PROJECT);
        }

        @Override
        public boolean isIncremental() {
            return true;
        }

        @Override
        public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
            TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
            transformInvocation.getInputs().stream().flatMap(new Function<TransformInput, Stream<QualifiedContent>>() {
                @Override
                public Stream<QualifiedContent> apply(TransformInput transformInput) {
                    return Stream.concat(transformInput.getJarInputs().stream(), transformInput.getDirectoryInputs().stream());
                }
            }).forEach(new Consumer<QualifiedContent>() {
                @Override
                public void accept(QualifiedContent jarInput) {
                    if (jarInput instanceof JarInput) {
                        File dest = outputProvider.getContentLocation(
                                jarInput.getFile().getAbsolutePath(),
                                jarInput.getContentTypes(),
                                jarInput.getScopes(),
                                Format.JAR);
                        try {
                            FileUtils.copyFile(jarInput.getFile(), dest);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (jarInput instanceof DirectoryInput) {
                        File dest = outputProvider.getContentLocation(jarInput.getName(),
                                jarInput.getContentTypes(), jarInput.getScopes(),
                                Format.DIRECTORY);
                        try {
                            FileUtils.copyFile(jarInput.getFile(), dest);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void apply(Project project) {
//        ClassWriter classWriter = new ClassWriter();
        project.getExtensions().getByType(AppExtension.class).registerTransform(new MyTransformer());
        project.getExtensions().getByType(AppExtension.class).registerTransform(new MyTransformer2());
//        project.getExtensions().getByType(AppExtension).
//        project.getExtensions().getByType(AppExtension.class).
    }
}
