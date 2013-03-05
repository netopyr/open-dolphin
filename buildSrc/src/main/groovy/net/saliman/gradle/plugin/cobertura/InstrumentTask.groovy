package net.saliman.gradle.plugin.cobertura

/**
 * hijacked class from : https://github.com/stevesaliman/gradle-cobertura-plugin/blob/master/src/main/groovy/net/saliman/gradle/plugin/cobertura/InstrumentTask.groovy
 * In order to use provided converageDirs from the extension, line 40 has to be changes:
 * original:
 * project.files(project.sourceSets.main.output.classesDir.path)
 * modified:
 * project.files(getClassesDirs())
 * Pull Request opend : https://github.com/stevesaliman/gradle-cobertura-plugin/pull/9
 */
import org.gradle.api.DefaultTask

import org.gradle.api.tasks.TaskAction

/**
 * Gradle task that instruments java sources for Cobertura coverage reports.
 * <p>
 * The plugin will always add this task as a dependency of test tasks, but
 * we only want to instrument classes if the user has elected to run the
 * "cobertura" task created by the plugin.  To accomplish this, the task
 * action looks for the presence of a runner when we try to instrument.
 */
class InstrumentTask extends DefaultTask {
	File destinationDir
	CoberturaExtension configuration
	def runner

//	@InputFiles
	def getClassesDirs() {
		configuration.coverageDirs
	}

//	@OutputFile
	def getDatafile () {
		configuration.coverageDatafile
	}

	@TaskAction
	def instrument() {
		project.logger.info("Instrumenting code...")
		// Before we instrument, copy from the main source to the instrumented path.
		// Doing this here means we only need to do it when we need to instrument.
		def instrumentDirs = [] as Set
        project.logger.lifecycle('Using patched InstrumentTask')
        project.logger.info("Use configured classpaths : $classesDirs")
		project.files(getClassesDirs()).each { File f ->
			if (f.isDirectory()) {
				// Copy directories from main source to instrumented path
				project.copy {
					from f
					into "${project.buildDir}/instrumented_classes"
				}
			} else {
				// add files to the instrumented dir list.
				instrumentDirs << f.path
			}
		}
		// add the instrumented dir to the list.
		instrumentDirs << ("${project.buildDir}/instrumented_classes" as String)

		runner.instrument null, getDatafile().path, getDestinationDir()?.path,
						configuration.coverageIgnores as List,
						configuration.coverageIncludes as List,
						configuration.coverageExcludes as List, instrumentDirs as List
	}
}
