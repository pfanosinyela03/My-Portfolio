import React from 'react';
import { SlidersHorizontal, ArrowUpRight, Mail } from 'lucide-react';
import { PortfolioProfile } from '../data/portfolioData';

interface NavbarProps {
  profile: PortfolioProfile;
  onOpenEdit: () => void;
  onContactClick: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ profile, onOpenEdit, onContactClick }) => {
  return (
    <header className="sticky top-0 z-40 w-full border-b border-slate-800/80 bg-slate-950/85 backdrop-blur-md">
      <div className="max-w-7xl mx-auto px-6 h-18 flex items-center justify-between gap-8">
        {/* Zone 1: Brand Wordmark (Single text element) */}
        <a 
          href="#top" 
          className="text-lg font-bold tracking-tight text-white hover:text-blue-400 transition-colors whitespace-nowrap shrink-0"
        >
          {profile.name}
        </a>

        {/* Zone 2: Navigation Links (Clean single-line prose with hover underline) */}
        <nav className="hidden md:flex items-center gap-7 text-sm font-medium text-slate-300">
          <a href="#works" className="hover:text-white transition-colors whitespace-nowrap shrink-0">
            Selected Works
          </a>
          <a href="#skills" className="hover:text-white transition-colors whitespace-nowrap shrink-0">
            Technical Expertise
          </a>
          <a href="#experience" className="hover:text-white transition-colors whitespace-nowrap shrink-0">
            Experience
          </a>
          <a href="#about" className="hover:text-white transition-colors whitespace-nowrap shrink-0">
            About
          </a>
          <a href="#contact" className="hover:text-white transition-colors whitespace-nowrap shrink-0">
            Contact
          </a>
        </nav>

        {/* Zone 3: Primary Action & Customizer */}
        <div className="flex items-center gap-3 shrink-0">
          <button
            onClick={onOpenEdit}
            className="flex items-center gap-1.5 px-3 py-2 text-xs font-medium text-slate-300 bg-slate-900 border border-slate-700/80 rounded-lg hover:bg-slate-800 hover:text-white transition-colors whitespace-nowrap shrink-0"
            title="Customize your portfolio data and theme"
          >
            <SlidersHorizontal className="w-3.5 h-3.5 text-blue-400" />
            <span className="hidden sm:inline">Customize</span>
          </button>

          <button
            onClick={onContactClick}
            className="flex items-center gap-1.5 px-4 py-2 text-xs font-semibold text-white bg-blue-600 rounded-lg hover:bg-blue-500 shadow-sm shadow-blue-500/20 active:scale-95 transition-all whitespace-nowrap shrink-0"
          >
            <Mail className="w-3.5 h-3.5" />
            <span>Get in Touch</span>
          </button>
        </div>
      </div>
    </header>
  );
};
