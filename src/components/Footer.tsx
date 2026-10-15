import React from 'react';
import { ArrowUp, Github, Linkedin, Mail } from 'lucide-react';
import { PortfolioProfile } from '../data/portfolioData';

interface FooterProps {
  profile: PortfolioProfile;
}

export const Footer: React.FC<FooterProps> = ({ profile }) => {
  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <footer className="w-full border-t border-slate-800 bg-slate-950 py-12 text-slate-400 text-xs">
      <div className="max-w-7xl mx-auto px-6 flex flex-col md:flex-row items-center justify-between gap-6">
        {/* Brand & Rights */}
        <div className="space-y-1 text-center md:text-left">
          <div className="font-bold text-white text-sm tracking-tight">{profile.name}</div>
          <p className="text-slate-500">
            Senior Software Engineer & Technologist · Built with React 19 & Tailwind CSS
          </p>
        </div>

        {/* Quick Nav Links */}
        <div className="flex items-center gap-6 text-slate-300 font-medium">
          <a href="#works" className="hover:text-white transition-colors">Works</a>
          <a href="#skills" className="hover:text-white transition-colors">Expertise</a>
          <a href="#experience" className="hover:text-white transition-colors">Experience</a>
          <a href="#about" className="hover:text-white transition-colors">About</a>
          <a href="#contact" className="hover:text-white transition-colors">Contact</a>
        </div>

        {/* Socials & Back to Top */}
        <div className="flex items-center gap-4">
          <a
            href={`mailto:${profile.email}`}
            className="p-2 rounded-lg bg-slate-900 border border-slate-800 text-slate-300 hover:text-white hover:border-slate-700 transition-colors"
            title="Email"
          >
            <Mail className="w-4 h-4" />
          </a>
          <a
            href="https://github.com"
            target="_blank"
            rel="noreferrer"
            className="p-2 rounded-lg bg-slate-900 border border-slate-800 text-slate-300 hover:text-white hover:border-slate-700 transition-colors"
            title="GitHub"
          >
            <Github className="w-4 h-4" />
          </a>
          <a
            href="https://linkedin.com"
            target="_blank"
            rel="noreferrer"
            className="p-2 rounded-lg bg-slate-900 border border-slate-800 text-slate-300 hover:text-white hover:border-slate-700 transition-colors"
            title="LinkedIn"
          >
            <Linkedin className="w-4 h-4" />
          </a>
          <button
            onClick={scrollToTop}
            className="flex items-center gap-1 px-3 py-2 rounded-lg bg-slate-900 border border-slate-800 text-slate-300 hover:text-white hover:border-slate-700 transition-colors"
            title="Scroll to top"
          >
            <ArrowUp className="w-3.5 h-3.5" />
            <span>Top</span>
          </button>
        </div>
      </div>
    </footer>
  );
};
