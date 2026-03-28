package app.silva.patches.shared.layout.branding

import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.rawResourcePatch
import app.silva.util.inputStreamFromBundledResource
import java.nio.file.Files

/**
 * Copies the license and branding notice files to the target apk.
 */
internal val addLicensePatch = rawResourcePatch {
    execute {
        arrayOf(
            "SILVA_BRANDING.TXT",
            "SILVA_LICENSE.TXT",
            "SILVA_LICENSE_NOTICE.TXT"
        ).forEach { sourceFileName ->
            val inputFileStream = inputStreamFromBundledResource(
                "license",
                sourceFileName
            )!!

            val targetFile = get(sourceFileName, false).toPath()

            // Check if target file exists and give a more informative error
            // because Files.copy throws an exception if the file already exists.
            if (Files.exists(targetFile)) {
                throw PatchException("App is already modified with Silva patches ($targetFile already exists)")
            }

            Files.copy(inputFileStream, targetFile)
        }
    }
}
