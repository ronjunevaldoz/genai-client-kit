package io.github.ronjunevaldoz.genaiclient.elevenlabs

/** A Studio project: a long-form piece of content (audiobook, podcast, ...) split into chapters. */
public data class ElevenLabsProject(
    public val projectId: String,
    public val name: String,
    public val createDateUnix: Long?,
    public val defaultModelId: String?,
)

/** A section of a [ElevenLabsProject]'s content. */
public data class ElevenLabsChapter(
    public val chapterId: String,
    public val name: String,
)

/** A rendered audio snapshot of a [ElevenLabsChapter], as produced by [ElevenLabsProjectsClient.convertProject]. */
public data class ElevenLabsSnapshot(
    public val snapshotId: String,
    public val createdAtUnix: Long?,
)

internal fun ElevenLabsProjectDto.toProject(): ElevenLabsProject =
    ElevenLabsProject(projectId = projectId, name = name, createDateUnix = createDateUnix, defaultModelId = defaultModelId)

internal fun ElevenLabsChapterDto.toChapter(): ElevenLabsChapter = ElevenLabsChapter(chapterId = chapterId, name = name)

internal fun ElevenLabsSnapshotDto.toSnapshot(): ElevenLabsSnapshot =
    ElevenLabsSnapshot(snapshotId = snapshotId, createdAtUnix = createdAtUnix)
