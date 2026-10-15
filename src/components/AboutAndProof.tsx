import React from 'react';
import { Quote, Sparkles, Check, Globe } from 'lucide-react';
import { initialProfile, initialTestimonials } from '../data/portfolioData';

const portraitImage = new URL('../assets/images/avatar_pfano_sinyela_1791994638831.jpg', import.meta.url).href;

export const AboutAndProof: React.FC = () => {
  return (
    <section id="about" className="py-20 border-b border-slate-800/60 bg-slate-950/40">
      <div className="max-w-7xl mx-auto px-6 space-y-16">
        {/* About Bio Section */}
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
          {/* Portrait Column */}
          <div className="lg:col-span-5">
            <div className="relative mx-auto max-w-sm rounded-2xl overflow-hidden border border-slate-800/80 bg-slate-900 shadow-2xl">
              <div className="aspect-square w-full bg-slate-900 overflow-hidden">
                <img
                  src={portraitImage}
                  alt="Pfano Sinyela Portrait"
                  referrerPolicy="no-referrer"
                  className="w-full h-full object-cover"
                />
              </div>
              <div className="p-4 bg-slate-950/90 border-t border-slate-800 text-xs">
                <div className="font-bold text-white">Pfano Sinyela</div>
                <div className="text-slate-400">Senior Software Engineer & Technologist</div>
                <div className="mt-1 flex items-center gap-1.5 text-emerald-400 font-medium">
                  <span className="w-2 h-2 rounded-full bg-emerald-500" />
                  <span>Available for advisory & engineering roles</span>
                </div>
              </div>
            </div>
          </div>

          {/* Bio Text Column */}
          <div className="lg:col-span-7 space-y-6">
            <div className="text-xs font-semibold uppercase tracking-wider text-blue-400">
              Background & Principles
            </div>
            <h2 className="text-2xl sm:text-3xl lg:text-4xl font-bold tracking-tight text-white text-balance">
              Building with intentionality, velocity, and enduring software rigor.
            </h2>
            <p className="text-base text-slate-300 leading-relaxed">
              {initialProfile.bioParagraph1}
            </p>
            <p className="text-base text-slate-300 leading-relaxed">
              {initialProfile.bioParagraph2}
            </p>

            <div className="pt-2 grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs text-slate-300">
              <div className="p-3.5 rounded-xl bg-slate-900/60 border border-slate-800 flex items-start gap-2.5">
                <Check className="w-4 h-4 text-blue-400 shrink-0 mt-0.5" />
                <span>Zero tolerance for brittle mock code or unhandled edge cases</span>
              </div>
              <div className="p-3.5 rounded-xl bg-slate-900/60 border border-slate-800 flex items-start gap-2.5">
                <Check className="w-4 h-4 text-blue-400 shrink-0 mt-0.5" />
                <span>Deep empathy for user workflows and interface micro-physics</span>
              </div>
            </div>
          </div>
        </div>

        {/* Claim-to-Proof Adjacency: Attributable Testimonials */}
        <div className="pt-10 border-t border-slate-800/60">
          <div className="text-xs font-semibold uppercase tracking-wider text-blue-400 mb-6">
            Colleague & Client Endorsements
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {initialTestimonials.map((t, idx) => (
              <div
                key={idx}
                className="flex flex-col justify-between p-6 rounded-2xl border border-slate-800/80 bg-slate-900/50 hover:border-slate-700 transition-all space-y-6"
              >
                <div>
                  <Quote className="w-6 h-6 text-blue-400/50 mb-3" />
                  <p className="text-sm text-slate-300 leading-relaxed italic">
                    "{t.quote}"
                  </p>
                </div>

                <div className="pt-4 border-t border-slate-800/80 space-y-2">
                  <div className="text-xs">
                    <div className="font-bold text-white">{t.author}</div>
                    <div className="text-slate-400">{t.role} · {t.company}</div>
                  </div>
                  <div className="text-[11px] font-mono text-emerald-400 bg-emerald-950/40 border border-emerald-800/40 px-2 py-1 rounded inline-block">
                    {t.metricOutcome}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  );
};
