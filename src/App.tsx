import React, { useLayoutEffect, useState } from 'react';
import { projectsList, ProjectItem, pfanoProfile } from './data/pfanoData';
import { ModernNavbar } from './components/ModernNavbar';
import { ModernHero } from './components/ModernHero';
import { ModernAbout } from './components/ModernAbout';
import { ModernExperienceAndEducation } from './components/ModernExperienceAndEducation';
import { ModernSkills } from './components/ModernSkills';
import { ModernProjects } from './components/ModernProjects';
import { ModernContact } from './components/ModernContact';
import { ModernFooter } from './components/ModernFooter';
import { ModernProjectModal } from './components/ModernProjectModal';
import { ResumeModal } from './components/ResumeModal';
import { ModernToast } from './components/ModernToast';

export default function App() {
  const [selectedProject, setSelectedProject] = useState<ProjectItem | null>(null);
  const [isResumeModalOpen, setIsResumeModalOpen] = useState(false);
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  useLayoutEffect(() => {
    const sections = Array.from(
      document.querySelectorAll<HTMLElement>('main > section:not(#home)'),
    );
    const revealDirections = ['left', 'right', 'center'] as const;
    const revealTargets = sections.map((section, index) => {
      const target = section.firstElementChild as HTMLElement | null;

      if (target) {
        target.classList.add('scroll-reveal');
        target.dataset.reveal = revealDirections[index % revealDirections.length];
      }

      return target;
    }).filter((target): target is HTMLElement => target !== null);

    if (!('IntersectionObserver' in window)) {
      revealTargets.forEach((target) => target.classList.add('is-visible'));
      return;
    }

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add('is-visible');
            observer.unobserve(entry.target);
          }
        });
      },
      {
        threshold: 0.12,
        rootMargin: '0px 0px -8% 0px',
      },
    );

    revealTargets.forEach((target) => observer.observe(target));

    return () => observer.disconnect();
  }, []);

  const showToast = (message: string, duration = 3000) => {
    setToastMessage(message);
    setTimeout(() => {
      setToastMessage(null);
    }, duration);
  };

  const handleDownloadApk = (project: ProjectItem) => {
    if (!project.apkLink) {
      showToast(`APK download is not available for ${project.title}.`);
      return;
    }

    const fileName = project.apkLink.split('/').pop() ?? `${project.title.replace(/\s+/g, '_')}.apk`;
    showToast(`APK download initiated: ${fileName}`);

    const link = document.createElement('a');
    link.href = project.apkLink;
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleDownloadResume = () => {
    showToast("Downloading Pfano's CV...");
    const a = document.createElement('a');
    a.href = pfanoProfile.cvUrl;
    a.download = 'Pfano-Sinyela-CV.pdf';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  };

  const scrollToSection = (id: string) => {
    const el = document.getElementById(id);
    if (el) {
      el.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <div className="min-h-screen bg-[#F8FAFC] text-slate-900 flex flex-col font-sans selection:bg-blue-600 selection:text-white">
      {/* Top Navbar */}
      <ModernNavbar
        onContactClick={() => scrollToSection('contact')}
        onResumeClick={() => setIsResumeModalOpen(true)}
      />

      {/* Main Sections */}
      <main className="flex-1">
        <ModernHero
          onResumeClick={() => setIsResumeModalOpen(true)}
          onExploreProjects={() => scrollToSection('work')}
        />

        <ModernAbout
          onContactClick={() => scrollToSection('contact')}
        />

        <ModernExperienceAndEducation />

        <ModernSkills />

        <ModernProjects
          projects={projectsList}
          onSelectProject={(project) => setSelectedProject(project)}
          onDownloadApk={handleDownloadApk}
        />

        <ModernContact
          onShowToast={showToast}
        />
      </main>

      {/* Modern Footer */}
      <ModernFooter />

      {/* Project Lightbox & Slider Modal */}
      <ModernProjectModal
        project={selectedProject}
        onClose={() => setSelectedProject(null)}
        onDownloadApk={handleDownloadApk}
      />

      {/* CV Modal */}
      <ResumeModal
        isOpen={isResumeModalOpen}
        onClose={() => setIsResumeModalOpen(false)}
        onDownloadResume={handleDownloadResume}
      />

      {/* Toast Notification */}
      <ModernToast message={toastMessage} />
    </div>
  );
}
