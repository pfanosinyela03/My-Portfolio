import React, { useState, useEffect } from 'react';
import { Download, ArrowRight, Linkedin, Instagram, Github, Monitor, Terminal, Code2, Sparkles } from 'lucide-react';
import { dynamicTitles, movingTools, pfanoProfile } from '../data/pfanoData';

interface ModernHeroProps {
  onResumeClick: () => void;
  onExploreProjects: () => void;
}

export const ModernHero: React.FC<ModernHeroProps> = ({ onResumeClick, onExploreProjects }) => {
  const [titleIndex, setTitleIndex] = useState(0);
  const [fade, setFade] = useState(true);

  useEffect(() => {
    const interval = setInterval(() => {
      setFade(false);
      setTimeout(() => {
        setTitleIndex((prev) => (prev + 1) % dynamicTitles.length);
        setFade(true);
      }, 350);
    }, 3200);

    return () => clearInterval(interval);
  }, []);

  return (
    <section id="home" className="relative pt-12 pb-16 md:pt-16 md:pb-20 border-b border-slate-200/80 bg-white overflow-hidden">
      {/* Soft light decorative gradient background */}
      <div className="absolute top-0 right-1/4 w-[500px] h-[500px] bg-blue-50/70 rounded-full blur-3xl pointer-events-none -z-10" />
      <div className="absolute bottom-0 left-1/3 w-[450px] h-[350px] bg-indigo-50/60 rounded-full blur-3xl pointer-events-none -z-10" />

      <div className="max-w-6xl mx-auto px-6 space-y-12">
        {/* Intro Split Section */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 lg:gap-16 items-center">
          {/* Left Column: Headline & Intro */}
          <div className="lg:col-span-7 space-y-6">
            {/* Title with Smooth Dynamic Rotation */}
            <div>
              <h1 className="text-4xl sm:text-5xl lg:text-6xl font-bold tracking-tight text-slate-900 leading-[1.12]">
                <span className="block text-slate-900">Pfano Sinyela</span>
                <span
                  className={`block text-blue-600 transition-all duration-300 transform ${
                    fade ? 'opacity-100 translate-y-0' : 'opacity-0 -translate-y-2'
                  }`}
                >
                  {dynamicTitles[titleIndex]}
                </span>
              </h1>
            </div>

            {/* Tagline / Subtitle */}
            <p className="text-base sm:text-lg text-slate-600 leading-relaxed max-w-xl">
              Dedicated to building high-performance Android applications, scalable full-stack web platforms, and reliable database architectures with clean, modern code.
            </p>

            {/* CTA Buttons */}
            <div className="pt-2 flex flex-wrap items-center gap-4">
              <button
                onClick={onResumeClick}
                className="group relative inline-flex items-center gap-2.5 px-6 py-3.5 text-xs font-bold text-white bg-blue-600 hover:bg-blue-700 rounded-xl shadow-md shadow-blue-600/25 active:scale-98 transition-all cursor-pointer whitespace-nowrap shrink-0 overflow-hidden"
              >
                <Download className="w-4 h-4 group-hover:-translate-y-0.5 transition-transform" />
                <span>View my CV</span>
              </button>

              <button
                onClick={onExploreProjects}
                className="inline-flex items-center gap-2 px-5 py-3.5 text-xs font-bold text-slate-700 bg-slate-100 hover:bg-slate-200 border border-slate-200/80 rounded-xl transition-all cursor-pointer whitespace-nowrap shrink-0 active:scale-98"
              >
                <span>Explore Projects</span>
                <ArrowRight className="w-3.5 h-3.5 text-slate-500" />
              </button>
            </div>

            {/* Social Icons Row */}
            <div className="pt-2 flex items-center gap-3">
              <span className="text-xs font-medium text-slate-400 mr-2">Connect:</span>
              <a
                href={pfanoProfile.linkedin}
                target="_blank"
                rel="noreferrer"
                className="p-2.5 rounded-xl bg-slate-50 border border-slate-200 text-slate-600 hover:text-blue-600 hover:border-blue-200 hover:bg-blue-50/50 transition-all"
                title="LinkedIn"
                aria-label="Pfano LinkedIn profile"
              >
                <Linkedin className="w-4 h-4" />
              </a>

              <a
                href={pfanoProfile.github}
                target="_blank"
                rel="noreferrer"
                className="p-2.5 rounded-xl bg-slate-50 border border-slate-200 text-slate-600 hover:text-blue-600 hover:border-blue-200 hover:bg-blue-50/50 transition-all"
                title="GitHub"
                aria-label="Pfano GitHub profile"
              >
                <Github className="w-4 h-4" />
              </a>

              <a
                href={pfanoProfile.instagram}
                target="_blank"
                rel="noreferrer"
                className="p-2.5 rounded-xl bg-slate-50 border border-slate-200 text-slate-600 hover:text-pink-600 hover:border-pink-200 hover:bg-pink-50/50 transition-all"
                title="Instagram"
                aria-label="Pfano Instagram profile"
              >
                <Instagram className="w-4 h-4" />
              </a>
            </div>
          </div>

          {/* Right Column: Clean Workspace Setup Image (No card or overlay over it) */}
          <div className="lg:col-span-5 relative flex justify-center">
            <div className="relative w-full max-w-md rounded-3xl overflow-hidden border border-slate-200/80 shadow-xl shadow-slate-200/60 bg-slate-100">
              <img
                src={pfanoProfile.setupImage}
                alt="Developer Workstation"
                referrerPolicy="no-referrer"
                width={1536}
                height={1024}
                className="block w-full h-auto object-contain"
              />
            </div>
          </div>
        </div>

        {/* Moving Words Ticker Section (Tools: Flutter, Java, Databases, Kotlin, etc.) */}
        <div className="pt-4 border-t border-slate-200/80">
          <div className="flex items-center gap-3 mb-3">
            <div className="flex items-center gap-1.5 text-xs font-bold uppercase tracking-wider text-slate-400">
              <Terminal className="w-3.5 h-3.5 text-blue-600" />
              <span>Core Stack & Tools</span>
            </div>
            <div className="h-px flex-1 bg-slate-200/80" />
          </div>

          {/* Continuous Infinite Marquee Ribbon */}
          <div className="relative overflow-hidden w-full py-2 [mask-image:linear-gradient(to_right,transparent,black_10%,black_90%,transparent)]">
            <div className="animate-marquee flex items-center gap-4">
              {movingTools.map((tool, idx) => (
                <div
                  key={`m1-${idx}`}
                  className="flex items-center gap-2 px-4 py-2 rounded-xl bg-slate-50 hover:bg-blue-50/70 border border-slate-200/80 hover:border-blue-300 transition-colors whitespace-nowrap shrink-0 group cursor-default"
                >
                  <span className="w-1.5 h-1.5 rounded-full bg-blue-600 group-hover:scale-125 transition-transform" />
                  <span className="text-xs font-bold text-slate-800 group-hover:text-blue-700 transition-colors">
                    {tool.name}
                  </span>
                  <span className="text-[10px] text-slate-400 font-medium">
                    ({tool.category})
                  </span>
                </div>
              ))}

              {movingTools.map((tool, idx) => (
                <div
                  key={`m2-${idx}`}
                  className="flex items-center gap-2 px-4 py-2 rounded-xl bg-slate-50 hover:bg-blue-50/70 border border-slate-200/80 hover:border-blue-300 transition-colors whitespace-nowrap shrink-0 group cursor-default"
                >
                  <span className="w-1.5 h-1.5 rounded-full bg-blue-600 group-hover:scale-125 transition-transform" />
                  <span className="text-xs font-bold text-slate-800 group-hover:text-blue-700 transition-colors">
                    {tool.name}
                  </span>
                  <span className="text-[10px] text-slate-400 font-medium">
                    ({tool.category})
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* UNDER THOSE WORDS: Dark Landscape Setup Banner */}
        <div className="pt-2">
          <div className="relative rounded-3xl overflow-hidden border border-slate-800 bg-slate-950 shadow-2xl group">
            {/* 16:9 Dark Landscape Setup Image */}
            <div className="aspect-[16/8] sm:aspect-[16/7] md:aspect-[16/6] w-full overflow-hidden bg-slate-950">
              <img
                src={pfanoProfile.darkLandscapeSetup}
                alt="Dark Landscape Developer Studio Setup"
                referrerPolicy="no-referrer"
                className="w-full h-full object-cover object-center group-hover:scale-102 transition-transform duration-700 opacity-95"
              />
            </div>

            {/* Bottom Glass Badge Strip */}
            <div className="absolute inset-x-0 bottom-0 p-4 sm:p-6 bg-gradient-to-t from-slate-950 via-slate-950/80 to-transparent flex items-center text-xs">
              <div className="flex items-center gap-2.5 text-white">
                <div className="p-2 rounded-xl bg-blue-600/20 text-blue-400 border border-blue-500/30">
                  <Monitor className="w-4 h-4" />
                </div>
                <div className="font-bold text-sm text-white">Code. Build. Debug. Repeat</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
