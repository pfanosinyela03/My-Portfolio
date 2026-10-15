import React from 'react';
import { ArrowUp, Github, Linkedin, Instagram, Mail } from 'lucide-react';
import { pfanoProfile } from '../data/pfanoData';

export const ModernFooter: React.FC = () => {
  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <footer className="w-full border-t border-slate-200/80 bg-white py-12 text-slate-500 text-xs">
      <div className="max-w-6xl mx-auto px-6 flex flex-col md:flex-row items-center justify-between gap-6">
        {/* Brand & Rights */}
        <div className="text-center md:text-left space-y-1">
          <div className="font-bold text-slate-900 text-base">
            PFANO<span className="text-blue-600">.</span>
          </div>
          <p className="text-slate-500">
            {pfanoProfile.name} · Software Engineer
          </p>
          <p className="text-slate-400 text-[11px]">
            &#169; {new Date().getFullYear()} All rights reserved.
          </p>
        </div>

        {/* Quick Nav Links */}
        <div className="flex items-center gap-6 font-semibold text-slate-600">
          <a href="#home" className="hover:text-blue-600 transition-colors">Home</a>
          <a href="#about" className="hover:text-blue-600 transition-colors">About</a>
          <a href="#experience" className="hover:text-blue-600 transition-colors">Experience</a>
          <a href="#skills" className="hover:text-blue-600 transition-colors">Skills</a>
          <a href="#work" className="hover:text-blue-600 transition-colors">Projects</a>
          <a href="#contact" className="hover:text-blue-600 transition-colors">Contact</a>
        </div>

        {/* Social Links & Back to Top */}
        <div className="flex items-center gap-3">
          <a
            href={pfanoProfile.linkedin}
            target="_blank"
            rel="noreferrer"
            className="p-2.5 rounded-xl bg-slate-50 border border-slate-200 text-slate-600 hover:text-blue-600 hover:bg-blue-50/50 transition-colors"
            title="LinkedIn"
            aria-label="LinkedIn profile"
          >
            <Linkedin className="w-4 h-4" />
          </a>
          <a
            href={pfanoProfile.github}
            target="_blank"
            rel="noreferrer"
            className="p-2.5 rounded-xl bg-slate-50 border border-slate-200 text-slate-600 hover:text-slate-900 hover:bg-slate-100 transition-colors"
            title="GitHub"
            aria-label="GitHub profile"
          >
            <Github className="w-4 h-4" />
          </a>
          <a
            href={pfanoProfile.instagram}
            target="_blank"
            rel="noreferrer"
            className="p-2.5 rounded-xl bg-slate-50 border border-slate-200 text-slate-600 hover:text-pink-600 hover:bg-pink-50/50 transition-colors"
            title="Instagram"
            aria-label="Instagram profile"
          >
            <Instagram className="w-4 h-4" />
          </a>
          <button
            onClick={scrollToTop}
            className="flex items-center gap-1 px-3 py-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold transition-colors cursor-pointer ml-2"
            title="Back to top"
          >
            <ArrowUp className="w-3.5 h-3.5" />
            <span>Top</span>
          </button>
        </div>
      </div>
    </footer>
  );
};
