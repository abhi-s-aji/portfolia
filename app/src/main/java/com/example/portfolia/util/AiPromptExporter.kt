package com.example.portfolia.util

import com.example.portfolia.data.ProjectEntity
import com.example.portfolia.data.ReferenceEntity
import com.example.portfolia.data.UserProfileEntity

object AiPromptExporter {
    fun generateMasterPrompt(
        profile: UserProfileEntity?,
        projects: List<ProjectEntity>,
        references: List<ReferenceEntity>
    ): String {
        val name = profile?.name ?: "Developer"
        val title = profile?.title ?: "Software Engineer"
        val bio = profile?.bio ?: "Passionate mobile & web app builder."
        val email = profile?.email ?: ""
        val github = profile?.githubUrl ?: ""
        val linkedin = profile?.linkedinUrl ?: ""

        val projectsList = if (projects.isEmpty()) "No active projects listed." else projects.joinToString("\n\n") { p ->
            "### ${p.title} [${p.category}]\n- **Description**: ${p.description}\n- **Tech Stack**: ${p.techStack}\n- **GitHub**: ${p.githubUrl}"
        }

        val referencesList = if (references.isEmpty()) "No references listed." else references.joinToString("\n") { r ->
            "- [${r.title}](${r.url}) (${r.category}) - ${r.notes}"
        }

        return """
        # SYSTEM PROMPT: BUILD A HIGH-END DEVELOPER PORTFOLIO WEBSITE
        
        You are a World-Class Frontend Engineer and Web Designer. 
        Create a single-page modern portfolio website using HTML, Tailwind CSS, Lucide Icons, and Framer Motion / JavaScript.
        
        ## 1. DEVELOPER PROFILE
        - **Name**: $name
        - **Role/Title**: $title
        - **Bio**: $bio
        - **Email**: $email
        - **GitHub**: $github
        - **LinkedIn**: $linkedin
        
        ## 2. FEATURED PROJECTS
        $projectsList
        
        ## 3. SAVED LINKS & RESOURCES
        $referencesList
        
        ## 4. DESIGN & ARCHITECTURE SPECIFICATIONS
        - **Theme**: Dark Mode with Glassmorphism 2.0 (backdrop-filter blur, subtle specular border gradients).
        - **Color Palette**: Deep dark base `#0A0A0A`, warm purple `#764BA2` to soft rose `#FD79A8` glow highlights.
        - **Layout**: Sticky header with navigation, Hero Section with animated tagline, Interactive Project Cards grid with hover scale, Useful Links section, and Footer.
        - **Interactivity**: Dynamic search filter for projects, copy-to-clipboard contact buttons, and responsive drawer menu for mobile screens.
        
        Generate the full, single-file production HTML code ready for instant deployment on Vercel, Netlify, or Bolt.new!
        """.trimIndent()
    }
}
