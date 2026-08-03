package com.example.portfolia.util

import com.example.portfolia.data.ProjectEntity
import com.example.portfolia.data.UserProfileEntity
import com.example.portfolia.ui.theme.ThemeAccent

object AiPromptExporter {

    // ---------------------------------------------------------------------------
    // Color extraction
    // ---------------------------------------------------------------------------

    /**
     * Converts a Compose Color to an uppercase web hex string (e.g. "#1E1E20").
     *
     * Color.value is a ULong where R/G/B are stored as Float16 channel values,
     * NOT as packed ARGB bytes. The correct extraction path is via the typed
     * float properties Color.red, Color.green, Color.blue (each in 0.0..1.0).
     */
    private fun androidx.compose.ui.graphics.Color.toHex(): String {
        val r = (this.red * 255f).toInt().coerceIn(0, 255)
        val g = (this.green * 255f).toInt().coerceIn(0, 255)
        val b = (this.blue * 255f).toInt().coerceIn(0, 255)
        return "#%02X%02X%02X".format(r, g, b)
    }

    // ---------------------------------------------------------------------------
    // Theme mode detection
    // ---------------------------------------------------------------------------

    /**
     * Returns true if the accent's background is perceived as light.
     * Uses the standard relative luminance formula (BT.601 coefficients).
     * MINIMAL_STARK (0xFFFFFFFF) correctly resolves to true; all dark presets resolve to false.
     */
    private fun isLightTheme(vibe: ThemeAccent): Boolean {
        val lum = 0.299f * vibe.background.red +
                0.587f * vibe.background.green +
                0.114f * vibe.background.blue
        return lum > 0.5f
    }

    // ---------------------------------------------------------------------------
    // Smart color token resolution
    // ---------------------------------------------------------------------------

    /**
     * Returns a web-readable background hex.
     * Dark mode: replaces near-black values (including pure #000000) with a
     * deep obsidian canvas that renders better on web (#0A0A0B).
     * Light mode: returns #F8F9FA regardless of the stored value.
     */
    private fun resolveBackgroundHex(vibe: ThemeAccent, isLight: Boolean): String {
        if (isLight) return "#F8F9FA"
        val raw = vibe.background.toHex()
        return if (raw == "#000000") "#0A0A0B" else raw
    }

    /**
     * Returns a web-readable card surface hex.
     * Dark mode: replaces #000000 with a dark charcoal glass surface (#141415).
     * Light mode: returns #FFFFFF.
     */
    private fun resolveSurfaceHex(vibe: ThemeAccent, isLight: Boolean): String {
        if (isLight) return "#FFFFFF"
        val raw = vibe.surface.toHex()
        return if (raw == "#000000") "#141415" else raw
    }

    /**
     * Returns the user's accent highlight hex.
     * Falls back to sky blue (#38BDF8) if the resolved value is pure black.
     */
    private fun resolveAccentHex(vibe: ThemeAccent): String {
        val raw = vibe.primary.toHex()
        return if (raw == "#000000") "#38BDF8" else raw
    }

    /**
     * Returns high-contrast primary text hex appropriate for the active mode.
     */
    private fun resolvePrimaryTextHex(isLight: Boolean): String =
        if (isLight) "#0F172A" else "#FFFFFF"

    /**
     * Returns readable muted text hex appropriate for the active mode.
     */
    private fun resolveMutedTextHex(isLight: Boolean): String =
        if (isLight) "#64748B" else "#A1A1AA"

    // ---------------------------------------------------------------------------
    // Field sanitizers
    // ---------------------------------------------------------------------------

    private fun sanitizeName(raw: String?): String =
        raw?.trim()?.takeIf { it.isNotEmpty() } ?: "Developer"

    private fun sanitizeRole(raw: String?): String {
        val t = raw?.trim() ?: ""
        return if (t.length < 4) "Full Stack Developer" else t
    }

    private fun sanitizeBio(raw: String?): String {
        val t = raw?.trim() ?: ""
        return when {
            t.isEmpty() ->
                "Software engineer focused on building reliable, maintainable applications."
            t.length < 40 ->
                "Software engineer with expertise in $t, focused on building reliable and maintainable applications."
            else -> t
        }
    }

    /**
     * Formats contact fields as indented bullet lines, skipping blank entries.
     */
    private fun formatContactLinks(email: String, github: String, linkedin: String): String {
        return buildString {
            if (email.isNotBlank()) appendLine("  - Email: $email")
            if (github.isNotBlank()) appendLine("  - GitHub: $github")
            if (linkedin.isNotBlank()) appendLine("  - LinkedIn: $linkedin")
        }.trimEnd()
    }

    // ---------------------------------------------------------------------------
    // Project formatter
    // ---------------------------------------------------------------------------

    private fun formatProjectsList(projects: List<ProjectEntity>): String {
        if (projects.isEmpty()) {
            return "No projects listed. Render an exemplary placeholder project card " +
                    "with sample title, category badge, description text, and tech stack chips."
        }
        return projects.mapIndexed { index, p ->
            buildString {
                appendLine("Project ${index + 1}:")
                appendLine("- Title: ${p.title}")
                appendLine("- Category: ${p.category}")
                appendLine("- Description: ${p.description}")
                appendLine("- Tech Stack: ${p.techStack.joinToString(", ")}")
                if (p.githubUrl.isNotBlank()) appendLine("- GitHub URL: ${p.githubUrl}")
                if (p.demoUrl.isNotBlank()) appendLine("- Live Demo URL: ${p.demoUrl}")
                if (p.linkedinPostUrl.isNotBlank()) appendLine("- LinkedIn URL: ${p.linkedinPostUrl}")
            }.trimEnd()
        }.joinToString("\n\n")
    }

    // ---------------------------------------------------------------------------
    // Tech stack aggregator
    // ---------------------------------------------------------------------------

    private fun aggregateTechStack(projects: List<ProjectEntity>): String {
        val skills = projects
            .flatMap { it.techStack }
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()
        return if (skills.isEmpty()) {
            "No tech stack data extracted. Render a representative placeholder skill grid."
        } else {
            skills.joinToString(", ")
        }
    }

    // ---------------------------------------------------------------------------
    // Markdown export (Choice A — plain markdown card)
    // ---------------------------------------------------------------------------

    fun generateMarkdown(profile: UserProfileEntity?, projects: List<ProjectEntity>): String {
        val name = sanitizeName(profile?.name)
        val role = sanitizeRole(profile?.title)
        val bio = sanitizeBio(profile?.bio)
        val email = profile?.email?.trim() ?: ""
        val github = profile?.githubUrl?.trim() ?: ""
        val linkedin = profile?.linkedinUrl?.trim() ?: ""
        val exp = "${profile?.experienceYears ?: 0}+ years"

        val projectsSection = if (projects.isEmpty()) {
            "No active projects listed."
        } else {
            projects.joinToString("\n\n") { p ->
                buildString {
                    appendLine("### ${p.title} [${p.category}]")
                    appendLine("- Description: ${p.description}")
                    appendLine("- Tech Stack: ${p.techStack.joinToString(", ")}")
                    if (p.githubUrl.isNotBlank()) appendLine("- GitHub: ${p.githubUrl}")
                    if (p.demoUrl.isNotBlank()) appendLine("- Live Demo: ${p.demoUrl}")
                    if (p.linkedinPostUrl.isNotBlank()) appendLine("- LinkedIn: ${p.linkedinPostUrl}")
                }.trimEnd()
            }
        }

        return buildString {
            appendLine("# $name - Developer Portfolio")
            appendLine()
            appendLine("## Professional Identity")
            appendLine("- Role: $role")
            appendLine("- Bio: $bio")
            appendLine("- Experience: $exp")
            appendLine()
            appendLine("## Contact")
            if (email.isNotEmpty()) appendLine("- Email: $email")
            if (github.isNotEmpty()) appendLine("- GitHub: $github")
            if (linkedin.isNotEmpty()) appendLine("- LinkedIn: $linkedin")
            appendLine()
            appendLine("## Featured Projects")
            appendLine(projectsSection)
            appendLine("---")
            append("Generated via Portfolia Exporter Engine")
        }
    }

    // ---------------------------------------------------------------------------
    // Prompt 1/5 — Architecture & Theme Engine
    // ---------------------------------------------------------------------------

    fun generatePrompt1(vibe: ThemeAccent): String {
        val isLight = isLightTheme(vibe)
        val backgroundHex = resolveBackgroundHex(vibe, isLight)
        val cardSurfaceHex = resolveSurfaceHex(vibe, isLight)
        val accentHex = resolveAccentHex(vibe)
        val primaryTextHex = resolvePrimaryTextHex(isLight)
        val mutedTextHex = resolveMutedTextHex(isLight)

        return buildString {
            appendLine("[Prompt 1/5: Architecture & Theme Engine]")
            appendLine(
                "Set up a single-page developer portfolio website using React, Tailwind CSS, and Lucide Icons."
            )
            appendLine()
            appendLine("Configure the theme palette based on the developer's selected preferences:")
            appendLine("- Base Background Canvas: $backgroundHex")
            appendLine("- Card Surface Background: $cardSurfaceHex")
            appendLine("- Card Border: #2A2A2D with a 1px top specular border (border-t border-white/10)")
            appendLine("- Accent Highlight Color: $accentHex")
            appendLine("- Primary Text: $primaryTextHex")
            appendLine("- Muted Text: $mutedTextHex")
            appendLine()
            append(
                "Use clean typography (Monospace for titles and badges, Sans-Serif for body text). " +
                "Ensure sharp card corners (rounded-xl), custom dark scrollbars, and fluid spacing. " +
                "Set up Tailwind dark-mode configuration, spacing utilities, and glassmorphism layout guidelines."
            )
        }.trim()
    }

    // ---------------------------------------------------------------------------
    // Prompt 2/5 — Hero & Profile Header
    // ---------------------------------------------------------------------------

    fun generatePrompt2(profile: UserProfileEntity?): String {
        val userName = sanitizeName(profile?.name)
        val userRole = sanitizeRole(profile?.title)
        val userBio = sanitizeBio(profile?.bio)
        val email = profile?.email?.trim() ?: ""
        val github = profile?.githubUrl?.trim() ?: ""
        val linkedin = profile?.linkedinUrl?.trim() ?: ""
        val uptime = profile?.uptime?.trim()?.takeIf { it.isNotEmpty() } ?: "99.9%"
        val commits = profile?.commits?.trim()?.takeIf { it.isNotEmpty() } ?: "12.4k"
        val exp = "${profile?.experienceYears ?: 0}+ YRS"
        val formattedContactLinks = formatContactLinks(email, github, linkedin)

        return buildString {
            appendLine("[Prompt 2/5: Hero & Profile Header]")
            appendLine("Build an executive-style Hero section based on these details:")
            appendLine("- Name: $userName")
            appendLine("- Role: $userRole")
            appendLine("- Bio: $userBio")
            appendLine("- Contact Links:")
            appendLine(formattedContactLinks)
            appendLine()
            append(
                "Render a Bento-grid stats display with metrics " +
                "(Experience: $exp, System Uptime: $uptime, Commits: $commits). " +
                "Add smooth entrance animations using Framer Motion. " +
                "Include an interactive Copy Email button with visual checkmark feedback."
            )
        }.trim()
    }

    // ---------------------------------------------------------------------------
    // Prompt 3/5 — Featured Projects Showcase
    // ---------------------------------------------------------------------------

    fun generatePrompt3(projects: List<ProjectEntity>): String {
        val formattedProjectsList = formatProjectsList(projects)

        return buildString {
            appendLine("[Prompt 3/5: Featured Projects Showcase]")
            appendLine(
                "Create a grid-based Featured Projects Showcase section displaying the following " +
                "projects from the developer's database:"
            )
            appendLine()
            appendLine(formattedProjectsList)
            appendLine()
            append(
                "Each card should render the title, category badge, description text, tech stack chips, " +
                "and action links (GitHub Repository, LinkedIn Spotlight, Live Demo). " +
                "Include a pill-style category filter bar at the top of the grid."
            )
        }.trim()
    }

    // ---------------------------------------------------------------------------
    // Prompt 4/5 — Interactive Tech Stack & Skills
    // ---------------------------------------------------------------------------

    fun generatePrompt4(projects: List<ProjectEntity>): String {
        val formattedTechStackList = aggregateTechStack(projects)

        return buildString {
            appendLine("[Prompt 4/5: Interactive Tech Stack & Skills]")
            appendLine(
                "Create an interactive Tech Stack & Skills filter grid displaying the developer's " +
                "extracted tech stack:"
            )
            appendLine(formattedTechStackList)
            appendLine()
            append(
                "Render each skill as an interactive monospace tag chip. " +
                "Selecting a skill chip dynamically highlights it and filters the project grid " +
                "to display only projects that contain that skill tag."
            )
        }.trim()
    }

    // ---------------------------------------------------------------------------
    // Prompt 5/5 — Layout Assembly & Deployment
    // ---------------------------------------------------------------------------

    fun generatePrompt5(): String {
        return buildString {
            appendLine("[Prompt 5/5: Layout Assembly & Deployment]")
            append(
                "Assemble all previous sections into a fully responsive single-page application " +
                "with a floating glassmorphic navigation header, copy-to-clipboard fallback, " +
                "copyright footer, and setup instructions for Vercel or Netlify."
            )
        }.trim()
    }
}
