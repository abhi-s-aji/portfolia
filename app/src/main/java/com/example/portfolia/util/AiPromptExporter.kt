package com.example.portfolia.util

import com.example.portfolia.data.ProjectEntity
import com.example.portfolia.data.UserProfileEntity
import com.example.portfolia.ui.theme.ThemeAccent

object AiPromptExporter {
    
    fun generateMarkdown(profile: UserProfileEntity?, projects: List<ProjectEntity>): String {
        val name = profile?.name ?: "Developer"
        val title = profile?.title ?: "Software Engineer"
        val bio = profile?.bio ?: "Passionate mobile & web app builder."
        val email = profile?.email ?: ""
        val github = profile?.githubUrl ?: ""
        val linkedin = profile?.linkedinUrl ?: ""
        val exp = "${profile?.experienceYears ?: 0}+ YRS"
        
        val projectsList = if (projects.isEmpty()) "*No active projects listed.*" else projects.joinToString("\n\n") { p ->
            val links = mutableListOf<String>()
            if (p.githubUrl.isNotBlank()) links.add("[GitHub](${p.githubUrl})")
            if (p.demoUrl.isNotBlank()) links.add("[Live Demo](${p.demoUrl})")
            if (p.linkedinPostUrl.isNotBlank()) links.add("[LinkedIn Feature](${p.linkedinPostUrl})")
            
            """
            ### ${p.title} [${p.category}]
            - **Description**: ${p.description}
            - **Tech Stack**: ${p.techStack.joinToString(", ")}
            ${if (links.isNotEmpty()) "- **Links**: ${links.joinToString(" | ")}" else ""}
            """.trimIndent()
        }
        
        return """
        # $name - Developer Portfolio
        
        ## Professional Identity
        - **Title**: $title
        - **Bio**: $bio
        - **Experience**: $exp
        
        ## Social Profiles
        - **Email**: $email
        - **GitHub**: $github
        - **LinkedIn**: $linkedin
        
        ## Featured Projects
        $projectsList
        
        ---
        *Generated via Portfolia V1 Exporter Engine*
        """.trimIndent()
    }

    fun generatePrompt1(vibe: ThemeAccent): String {
        val bgHex = String.format("#%06X", 0xFFFFFF and vibe.background.value.toLong().toInt()).uppercase()
        val surfHex = String.format("#%06X", 0xFFFFFF and vibe.surface.value.toLong().toInt()).uppercase()
        val accHex = String.format("#%06X", 0xFFFFFF and vibe.primary.value.toLong().toInt()).uppercase()
        val txtHex = String.format("#%06X", 0xFFFFFF and vibe.textPrimary.value.toLong().toInt()).uppercase()
        val mutHex = String.format("#%06X", 0xFFFFFF and vibe.textMuted.value.toLong().toInt()).uppercase()

        return """
        [Prompt 1/5: Architecture & Theme Engine]
        Set up a single-page portfolio website project. Configure Tailwind CSS with the following custom color palette vibe:
        - Base Background Canvas: $bgHex
        - Solid Obsidian Card Surfaces: $surfHex
        - Accent Highlight Theme Color: $accHex
        - Text Primary Color: $txtHex
        - Text Muted Color: $mutHex
        
        Configure standard web fonts (monospace titles, clean sans-serif body text). Set up dark-mode configuration, spacing utilities, and layout guidelines. Design a card component with a 1px specular top border (`border-t border-white/8`) and sharp rounded corners.
        """.trimIndent()
    }

    fun generatePrompt2(profile: UserProfileEntity?): String {
        val name = profile?.name ?: "Developer"
        val title = profile?.title ?: "Software Engineer"
        val bio = profile?.bio ?: "Passionate developer."
        val email = profile?.email ?: ""
        val github = profile?.githubUrl ?: ""
        val linkedin = profile?.linkedinUrl ?: ""
        val exp = "${profile?.experienceYears ?: 0}+ YRS"
        
        return """
        [Prompt 2/5: Hero & Profile Header]
        Build a high-end portfolio Hero section and profile header based on these details:
        - Name: $name
        - Title / Role: $title
        - Bio: $bio
        - Experience Years: $exp
        - Contact Links:
          - Email: $email
          - GitHub: $github
          - LinkedIn: $linkedin
          
        Implement a modern executive layout containing bento grid profile stats (e.g. Experience: $exp, Uptime: 99.9%, Commits: 12.4k). Add smooth entrance animations using CSS transitions or Framer Motion for social action buttons.
        """.trimIndent()
    }

    fun generatePrompt3(projects: List<ProjectEntity>): String {
        val projectsList = if (projects.isEmpty()) "No projects listed." else projects.joinToString("\n\n") { p ->
            """
            - Title: ${p.title}
            - Category: ${p.category}
            - Description: ${p.description}
            - Tech Stack: ${p.techStack.joinToString(", ")}
            - GitHub URL: ${p.githubUrl}
            - Live Demo URL: ${p.demoUrl}
            - LinkedIn URL: ${p.linkedinPostUrl}
            """.trimIndent()
        }
        
        return """
        [Prompt 3/5: Featured Projects Showcase]
        Create a grid-based Featured Projects Showcase section displaying these projects:
        $projectsList
        
        Each project card should clearly render:
        - Title and Category label
        - Description text
        - Small tags/chips for each tech stack item
        - Call-to-action buttons/icons:
          - "Live Demo" link (if present)
          - "GitHub Repository" link (if present)
          - "LinkedIn Spotlight" post link (if present)
          
        Ensure there is a clean pill-style filter bar at the top of the projects grid to filter projects reactively by category.
        """.trimIndent()
    }

    fun generatePrompt4(projects: List<ProjectEntity>): String {
        val allSkills = projects.flatMap { it.techStack }.distinct().joinToString(", ")
        return """
        [Prompt 4/5: Interactive Tech Stack & Skills]
        Create an interactive Skills selection list or filter grid.
        Here is the developer's tech stack extracted from their featured projects:
        $allSkills
        
        Render each skill as an interactive tag chip. Selecting a skill chip should dynamically highlight it and filter the projects grid down to only display projects that contain that skill tag.
        """.trimIndent()
    }

    fun generatePrompt5(): String {
        return """
        [Prompt 5/5: Layout Assembly & Deployment]
        Assemble all previous sections (Architecture/Theme, Hero Header, Projects Showcase, Skills Filter Grid) into a fully responsive, pixel-perfect single-page application.
        Include a floating sticky navigation header and a copyright footer. Add smooth scroll triggers and a copy-to-clipboard contact fallback for in-person networking. Provide setup instructions to host on Netlify or Vercel.
        """.trimIndent()
    }
}
