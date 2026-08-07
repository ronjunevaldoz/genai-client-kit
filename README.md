# genai-client-kit

A Kotlin Multiplatform client library for generative media APIs — ElevenLabs (speech), Suno
(music), and ByteDance (image) — over each provider's official REST/WebSocket API. Targets JVM,
Android, iOS (arm64 + simulator arm64), JS, and WasmJs.

> Status: `0.1.0`, pre-1.0. Per SemVer, the public API may still change without a major
> version bump until `1.0.0` ships. ElevenLabs's full public REST surface is covered: STT, TTS,
> realtime streaming, voices, Conversational AI agents (including knowledge base and Twilio
> outbound calling), speech-to-speech, sound effects, audio isolation, dubbing, models listing,
> account/subscription info, text-to-voice, the shared voice library, generation history, forced
> alignment, the Music API, pronunciation dictionaries, and Studio projects. Webhook listing is
> covered; webhook creation/update/deletion is a dashboard-only action with no public REST endpoint.
> Suno and ByteDance currently ship as contract-only stubs (see [Modules](#modules)).

## Install

```kotlin
dependencies {
    implementation(platform("io.github.ronjunevaldoz:genai-client-kit-bom:0.1.0"))
    implementation("io.github.ronjunevaldoz:genai-client-kit-elevenlabs")
}
```

Or without the BOM, pin each artifact's version directly:

```kotlin
dependencies {
    implementation("io.github.ronjunevaldoz:genai-client-kit-elevenlabs:0.1.0")
}
```

## Modules

| Module | Purpose |
|---|---|
| `genai-client-kit-core` | Shared contracts: `TranscriptionClient`, `SpeechClient`, `RealtimeSession`, `GenAiError` |
| `genai-client-kit-network` | Shared Ktor HTTP + WebSocket client |
| `genai-client-kit-elevenlabs` | `ElevenLabsTranscriptionClient` (Scribe STT), `ElevenLabsSpeechClient` (TTS), `ElevenLabsRealtimeClient` (realtime streaming TTS), `ElevenLabsVoicesClient` (voice CRUD/cloning), `ElevenLabsAgentsClient` + `ElevenLabsConversationsClient` + `ElevenLabsKnowledgeBaseClient` + `ElevenLabsOutboundCallClient` (Conversational AI), `ElevenLabsSpeechToSpeechClient` (voice changer), `ElevenLabsSoundEffectsClient`, `ElevenLabsAudioIsolationClient`, `ElevenLabsDubbingClient`, `ElevenLabsModelsClient`, `ElevenLabsUserClient`, `ElevenLabsTextToVoiceClient` (voice design), `ElevenLabsVoiceLibraryClient`, `ElevenLabsHistoryClient`, `ElevenLabsForcedAlignmentClient`, `ElevenLabsMusicClient`, `ElevenLabsPronunciationDictionariesClient`, `ElevenLabsProjectsClient` (Studio), `ElevenLabsWebhooksClient` |
| `genai-client-kit-suno` | `MusicGenerationClient` contract; `SunoClient` is a stub pending endpoint confirmation |
| `genai-client-kit-bytedance` | `ImageGenerationClient` contract; `ByteDanceClient` is a stub pending endpoint confirmation |
| `genai-client-kit-bom` | Version-aligns all of the above |

## Usage

### Speech-to-text (Scribe)

```kotlin
val client = ElevenLabsTranscriptionClient(apiKey = "...")

val result = client.transcribe(
    TranscriptionRequest(
        audio = audioBytes,
        filename = "clip.mp3",
        mimeType = "audio/mpeg",
        model = ElevenLabsModels.SCRIBE_V1, // or SCRIBE_V2
    ),
)
println(result.text)
```

### Text-to-speech

```kotlin
val client = ElevenLabsSpeechClient(apiKey = "...")

val audioBytes = client.synthesizeSpeech(
    SpeechRequest(input = "Hello there", model = ElevenLabsModels.ELEVEN_TURBO_V2_5, voice = "voice-id"),
)

// or stream chunks as they arrive:
client.synthesizeSpeechStream(request).collect { chunk -> play(chunk) }
```

### Realtime streaming TTS

```kotlin
val realtime = ElevenLabsRealtimeClient(apiKey = "...")
val session = realtime.connect(voiceId = "voice-id")

launch {
    session.events.collect { event ->
        if (event is RealtimeEvent.Audio) play(event.bytes)
    }
}

session.send(RealtimeInputChunk.Text("Streaming this "))
session.send(RealtimeInputChunk.Text("as it's typed."))
session.flush()
session.close()
```

### Voices

```kotlin
val voices = ElevenLabsVoicesClient(apiKey = "...")

val library = voices.listVoices()
val cloned = voices.addVoice(name = "My Voice", samples = listOf(VoiceSample("sample.mp3", "audio/mpeg", audioBytes)))
voices.editVoiceSettings(cloned.voiceId, VoiceSettings(stability = 0.6, similarityBoost = 0.8))
```

### Conversational AI

```kotlin
val agents = ElevenLabsAgentsClient(apiKey = "...")
val agent = agents.createAgent(
    name = "Support Bot",
    conversationConfig = buildJsonObject { put("first_message", "Hi, how can I help?") },
)

val conversations = ElevenLabsConversationsClient(apiKey = "...")
val page = conversations.listConversations(agentId = agent.agentId)
val detail = conversations.getConversation(page.conversations.first().conversationId)
```

### Speech-to-speech, sound effects, audio isolation, dubbing

```kotlin
val sts = ElevenLabsSpeechToSpeechClient(apiKey = "...")
val revoiced = sts.convert(SpeechToSpeechRequest(audio = clipBytes, filename = "clip.mp3", mimeType = "audio/mpeg", voiceId = "voice-id", model = ElevenLabsModels.ELEVEN_MULTILINGUAL_V2))

val effects = ElevenLabsSoundEffectsClient(apiKey = "...")
val doorCreak = effects.generate(SoundEffectRequest(text = "a door creaking open"))

val isolation = ElevenLabsAudioIsolationClient(apiKey = "...")
val voiceOnly = isolation.isolate(AudioIsolationRequest(audio = noisyClipBytes, filename = "clip.mp3", mimeType = "audio/mpeg"))

val dubbing = ElevenLabsDubbingClient(apiKey = "...")
val job = dubbing.createDubbing(DubbingSource("clip.mp4", "video/mp4", videoBytes), targetLanguage = "es")
// poll dubbing.getDubbingStatus(job.dubbingId) until status == "dubbed", then:
val dubbedAudio = dubbing.getDubbedAudio(job.dubbingId, languageCode = "es")
```

### Conversational AI: knowledge base and outbound calling

```kotlin
val knowledgeBase = ElevenLabsKnowledgeBaseClient(apiKey = "...")
val doc = knowledgeBase.createFromText(name = "FAQ", text = "Q: ... A: ...")

val agents = ElevenLabsAgentsClient(apiKey = "...")
val currentConfig = agents.getAgent(agent.agentId)["conversation_config"]!!.jsonObject
agents.updateAgent(agent.agentId, currentConfig.withKnowledgeBaseDocument(doc))

val calls = ElevenLabsOutboundCallClient(apiKey = "...")
val phoneNumber = calls.listPhoneNumbers().first()
calls.placeCall(agentId = agent.agentId, agentPhoneNumberId = phoneNumber.phoneNumberId, toNumber = "+15551234567")
```

### Models and account info

```kotlin
val models = ElevenLabsModelsClient(apiKey = "...").listModels()

val user = ElevenLabsUserClient(apiKey = "...")
val subscription = user.getSubscription()
println("${subscription.characterCount}/${subscription.characterLimit} characters used")
```

### Voice design, voice library, history, forced alignment

```kotlin
val textToVoice = ElevenLabsTextToVoiceClient(apiKey = "...")
val previews = textToVoice.createPreviews(voiceDescription = "A calm, warm narrator")
val newVoice = textToVoice.createVoiceFromPreview("Narrator", "A calm, warm narrator", previews.first())

val library = ElevenLabsVoiceLibraryClient(apiKey = "...")
val results = library.search(language = "en", gender = "female")
library.addSharedVoice(results.voices.first().publicOwnerId, results.voices.first().voiceId, newName = "My Copy")

val history = ElevenLabsHistoryClient(apiKey = "...")
val page = history.listHistory()
val audio = history.getHistoryItemAudio(page.items.first().historyItemId)

val alignment = ElevenLabsForcedAlignmentClient(apiKey = "...")
val timings = alignment.align(audio = clipBytes, filename = "clip.mp3", mimeType = "audio/mpeg", text = "Hello there")
```

### Music and pronunciation dictionaries

```kotlin
val music = ElevenLabsMusicClient(apiKey = "...")
val plan = music.createCompositionPlan(prompt = "An upbeat synthwave track", musicLengthMs = 30000)
val clip = music.compose(compositionPlan = plan)
// or straight from a prompt, skipping the plan step:
val quickClip = music.compose(prompt = "A calm lo-fi beat", musicLengthMs = 15000)

val dictionaries = ElevenLabsPronunciationDictionariesClient(apiKey = "...")
val dictionary = dictionaries.createFromRules(
    name = "Product Names",
    rules = listOf(PronunciationRule("Kubernetes", PronunciationRuleType.ALIAS, alias = "koo-ber-NET-eez")),
)
// then pass dictionary.id into a TTS/dubbing request via provider-specific options once supported
```

### Studio projects and webhooks

```kotlin
val projects = ElevenLabsProjectsClient(apiKey = "...")
val project = projects.createProject(name = "My Audiobook", defaultParagraphVoiceId = "voice-id")
projects.convertProject(project.projectId)
val chapters = projects.listChapters(project.projectId)
val snapshots = projects.listChapterSnapshots(project.projectId, chapters.first().chapterId)
val audio = projects.getChapterSnapshotAudio(project.projectId, chapters.first().chapterId, snapshots.first().snapshotId)

val webhooks = ElevenLabsWebhooksClient(apiKey = "...").listWebhooks()
```

## Testing

`genai-client-kit-elevenlabs` has 23 test files / 67 tests covering every client method against a
mocked Ktor engine (`ktor-client-mock`): happy path, streaming chunk reassembly, and the shared
`GenAiError` mapping (404, 500, transport failure) tested once rather than duplicated per client.

One gap: `ElevenLabsRealtimeClient`'s live WebSocket session has no integration test —
`ktor-client-mock` doesn't support the WS upgrade handshake. `ElevenLabsRealtimeDtoTest` instead
locks down the outbound/inbound message wire contract (serialization round-trip) that the session
encodes and decodes.

```bash
./gradlew :genai-client-kit-elevenlabs:jvmTest
```

## API surface rules

- `explicitApi()` is enforced on every published module.
- Public API changes require `./gradlew apiDump`; CI's `apiCheck` blocks merge otherwise.
- Pre-1.0: breaking changes may ship without a major version bump, per SemVer §2.4.

## Build

```bash
./gradlew build           # compile + test all targets
./gradlew ktlintCheck detekt   # lint
./gradlew apiCheck         # binary compatibility
```

## License

Apache-2.0
