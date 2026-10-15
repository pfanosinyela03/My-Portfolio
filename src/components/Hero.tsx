import React, { useState } from 'react';
import { ArrowDown, Download, Check, Copy, ExternalLink, Terminal, ShieldCheck } from 'lucide-react';
import { PortfolioProfile } from '../data/portfolioData';

interface HeroProps {
  profile: PortfolioProfile;
  onExploreWorks: () => void;
  onContactClick: () => void;
}

export const Hero: React.FC<HeroProps> = ({ profile, onExploreWorks, onContactClick }) => {
  const [copied, setCopied] = useState(false);

  const handleCopyEmail = () => {
    navigator.clipboard.writeText(profile.email);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <section id="top" className="relative pt-12 pb-16 md:pt-16 md:pb-24 border-b border-slate-800/60 overflow-hidden">
      {/* Background glow subtlety */}
      <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[700px] h-[350px] bg-blue-600/10 blur-[130px] pointer-events-none rounded-full" />

      <div className="max-w-7xl mx-auto px-6">
        {/* Split Hero Layout */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 lg:gap-16 items-center">
          {/* Left Column: Typographic Hierarchy */}
          <div className="lg:col-span-7 space-y-6">
            {/* Unboxed Status Metadata (Zero-pill discipline) */}
            <div className="flex flex-wrap items-center gap-2 text-xs font-medium text-slate-400">
              <span className="text-blue-400 font-semibold">{profile.role}</span>
              <span aria-hidden="true">·</span>
              <span>{profile.location}</span>
              <span aria-hidden="true">·</span>
              <span className="text-emerald-400 font-medium">{profile.availability}</span>
            </div>

            {/* Marquee Headline */}
            <h1 className="text-3xl sm:text-4xl lg:text-5xl font-bold tracking-tight text-white leading-[1.15] text-balance">
              {profile.bioHeadline}
            </h1>

            {/* Editorial Lead Prose */}
            <p className="text-base sm:text-lg text-slate-300 leading-relaxed max-w-2xl">
              {profile.bioParagraph1}
            </p>

            {/* Primary Action Buttons */}
            <div className="pt-2 flex flex-wrap items-center gap-4">
              <button
                onClick={onExploreWorks}
                className="inline-flex items-center gap-2 px-6 py-3 text-sm font-semibold text-white bg-blue-600 rounded-lg hover:bg-blue-500 shadow-md shadow-blue-600/25 active:scale-98 transition-all whitespace-nowrap shrink-0"
              >
                <span>View Selected Works</span>
                <ArrowDown className="w-4 h-4" />
              </button>

              <button
                onClick={handleCopyEmail}
                className="inline-flex items-center gap-2 px-4 py-3 text-sm font-medium text-slate-200 bg-slate-900 border border-slate-700/80 rounded-lg hover:bg-slate-800 hover:text-white transition-colors whitespace-nowrap shrink-0"
                title="Copy email to clipboard"
              >
                {copied ? <Check className="w-4 h-4 text-emerald-400" /> : <Copy className="w-4 h-4 text-slate-400" />}
                <span>{copied ? 'Email Copied' : profile.email}</span>
              </button>

              <a
                href="#contact"
                className="inline-flex items-center gap-1.5 px-4 py-3 text-sm font-medium text-slate-400 hover:text-white transition-colors whitespace-nowrap shrink-0"
              >
                <span>Schedule Call</span>
                <ExternalLink className="w-3.5 h-3.5" />
              </a>
            </div>
          </div>

          {/* Right Column: Visual Focal Carrier with Matching Height */}
          <div className="lg:col-span-5 relative">
            <div className="relative rounded-2xl overflow-hidden border border-slate-800/80 bg-slate-900/50 shadow-2xl group">
              {/* Media Container */}
              <div className="aspect-[16/11] sm:aspect-[16/10] lg:aspect-[4/3] w-full overflow-hidden bg-slate-900">
                <img
                  src="/src/assets/images/hero_tech_platform_1791994460029.jpg"
                  alt="Aether Flow Systems Architecture & Real-Time Telemetry Platform"
                  referrerPolicy="no-referrer"
                  className="w-full h-full object-cover object-center group-hover:scale-102 transition-transform duration-500"
                />
              </div>

              {/* Scrim Overlay & Floating Invariant Status */}
              <div className="absolute inset-x-0 bottom-0 p-5 bg-gradient-to-t from-slate-950 via-slate-950/80 to-transparent">
                <div className="flex items-center justify-between text-xs">
                  <div className="flex items-center gap-2 text-slate-300">
                    <Terminal className="w-3.5 h-3.5 text-blue-400" />
                    <span className="font-medium">Active Deployment: Aether Core</span>
                  </div>
                  <span className="font-mono text-emerald-400 tabular-nums">P99: 42ms</span>
                </div>
                <div className="mt-2 text-xs text-slate-400 flex items-center justify-between">
                  <span>52,000 ops / sec throughput</span>
                  <span className="flex items-center gap-1 text-slate-400">
                    <ShieldCheck className="w-3 h-3 text-blue-400" />
                    <span>0 security vulnerabilities</span>
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Full-Width 4-Column Proof & Metrics Strip (Placed strictly below hero split) */}
        <div className="mt-16 pt-10 border-t border-slate-800/70 grid grid-cols-2 lg:grid-cols-4 gap-8">
          {profile.metrics.map((metric, idx) => (
            <div key={idx} className="space-y-1">
              <div className="text-3xl lg:text-4xl font-bold font-mono tracking-tight text-white tabular-nums">
                {metric.value}
              </div>
              <div className="text-sm font-semibold text-slate-200">
                {metric.label}
              </div>
              <div className="text-xs text-slate-400 leading-snug">
                {metric.detail}
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};
