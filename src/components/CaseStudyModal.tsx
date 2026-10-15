import React, { useEffect, useState } from 'react';
import { X, ExternalLink, Github, CheckCircle2, Activity, Play, RefreshCw } from 'lucide-react';
import { Project } from '../data/portfolioData';

interface CaseStudyModalProps {
  project: Project | null;
  onClose: () => void;
}

export const CaseStudyModal: React.FC<CaseStudyModalProps> = ({ project, onClose }) => {
  const [simulatedOps, setSimulatedOps] = useState<number>(52000);
  const [isSimulating, setIsSimulating] = useState<boolean>(false);
  const [statusLog, setStatusLog] = useState<string>('Cluster operational · Invariants verified');

  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [onClose]);

  if (!project) return null;

  const runSimulation = () => {
    setIsSimulating(true);
    setStatusLog('Dispatching distributed load test...');
    setTimeout(() => {
      setSimulatedOps(prev => prev + Math.floor(Math.random() * 4000) + 1200);
      setStatusLog('P99 latency nominal (38ms) · 0 dropped packets');
      setIsSimulating(false);
    }, 600);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6 md:p-10 bg-slate-950/80 backdrop-blur-md overflow-y-auto">
      <div 
        className="relative w-full max-w-4xl my-auto bg-slate-900 border border-slate-700/80 rounded-2xl shadow-2xl overflow-hidden text-slate-100"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Top Header bar with Close Button */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-800 bg-slate-950/70">
          <div>
            <div className="text-xs font-semibold text-blue-400">
              {project.category} · Architecture Case Study
            </div>
            <h3 className="text-lg font-bold text-white">{project.title}</h3>
          </div>
          <button
            onClick={onClose}
            className="p-2 text-slate-400 hover:text-white hover:bg-slate-800 rounded-lg transition-colors"
            aria-label="Close modal"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Modal Body */}
        <div className="max-h-[75vh] overflow-y-auto p-6 space-y-8">
          {/* Hero Banner */}
          <div className="relative aspect-[16/8] rounded-xl overflow-hidden border border-slate-800 bg-slate-950">
            <img
              src={project.image}
              alt={project.title}
              referrerPolicy="no-referrer"
              className="w-full h-full object-cover"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-slate-950/90 via-slate-950/30 to-transparent" />
            <div className="absolute bottom-4 left-4 right-4 flex items-center justify-between">
              <span className="text-sm font-medium text-white drop-shadow-md">{project.tagline}</span>
              <span className="text-xs font-mono text-blue-300 bg-slate-950/80 px-2.5 py-1 rounded border border-slate-700/60">
                {project.impact}
              </span>
            </div>
          </div>

          {/* Project Details Grid */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 p-4 rounded-xl bg-slate-950/60 border border-slate-800/80 text-xs">
            <div>
              <div className="text-slate-500">Client / Org</div>
              <div className="font-semibold text-slate-200 mt-0.5">{project.client}</div>
            </div>
            <div>
              <div className="text-slate-500">Timeline</div>
              <div className="font-semibold text-slate-200 mt-0.5">{project.year}</div>
            </div>
            <div>
              <div className="text-slate-500">Engineering Role</div>
              <div className="font-semibold text-slate-200 mt-0.5">Lead Architect</div>
            </div>
            <div>
              <div className="text-slate-500">Status</div>
              <div className="font-semibold text-emerald-400 mt-0.5">In Production</div>
            </div>
          </div>

          {/* Problem & Solution Flow */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="p-5 rounded-xl bg-slate-950/50 border border-slate-800/80 space-y-2">
              <div className="text-xs font-bold uppercase tracking-wider text-amber-400">
                The Engineering Challenge
              </div>
              <p className="text-sm text-slate-300 leading-relaxed">
                {project.challenge}
              </p>
            </div>
            <div className="p-5 rounded-xl bg-slate-950/50 border border-slate-800/80 space-y-2">
              <div className="text-xs font-bold uppercase tracking-wider text-emerald-400">
                The Architectural Solution
              </div>
              <p className="text-sm text-slate-300 leading-relaxed">
                {project.solution}
              </p>
            </div>
          </div>

          {/* Quantified Business Impact & Metrics */}
          <div>
            <h4 className="text-sm font-bold uppercase tracking-wider text-slate-300 mb-3">
              Quantified Production Outcomes
            </h4>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              {project.metrics.map((m, idx) => (
                <div key={idx} className="p-4 rounded-xl bg-slate-950/70 border border-slate-800 text-center">
                  <div className="text-2xl font-bold font-mono text-blue-400 tabular-nums">
                    {m.value}
                  </div>
                  <div className="text-xs text-slate-400 mt-1 font-medium">
                    {m.label}
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Interactive Live Architecture Sandbox */}
          <div className="p-5 rounded-xl bg-slate-950 border border-slate-800 space-y-3">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2 text-xs font-semibold text-slate-200">
                <Activity className="w-4 h-4 text-blue-400" />
                <span>Interactive Telemetry Simulation</span>
              </div>
              <button
                onClick={runSimulation}
                disabled={isSimulating}
                className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-slate-200 bg-slate-800 hover:bg-slate-700 rounded-lg transition-colors disabled:opacity-50"
              >
                {isSimulating ? <RefreshCw className="w-3.5 h-3.5 animate-spin" /> : <Play className="w-3.5 h-3.5 text-emerald-400" />}
                <span>{isSimulating ? 'Testing...' : 'Test Cluster Load'}</span>
              </button>
            </div>
            <div className="p-3 bg-slate-900/90 rounded-lg border border-slate-800 font-mono text-xs flex items-center justify-between text-slate-300">
              <span>Ops: <span className="text-emerald-400 tabular-nums font-bold">{simulatedOps.toLocaleString()} / sec</span></span>
              <span className="text-slate-400">{statusLog}</span>
            </div>
          </div>

          {/* Tech Stack Unboxed */}
          <div className="pt-2">
            <div className="text-xs font-bold text-slate-400 mb-2">Technologies & Tooling:</div>
            <div className="flex flex-wrap gap-2 text-xs text-slate-300">
              {project.technologies.map((t, idx) => (
                <span key={idx} className="px-2.5 py-1 bg-slate-800/80 rounded border border-slate-700/60 font-mono">
                  {t}
                </span>
              ))}
            </div>
          </div>
        </div>

        {/* Modal Footer */}
        <div className="flex items-center justify-between px-6 py-4 border-t border-slate-800 bg-slate-950/80">
          <div className="text-xs text-slate-400">
            Case study archived for Pfano Sinyela Portfolio
          </div>
          <button
            onClick={onClose}
            className="px-5 py-2 text-xs font-semibold text-white bg-slate-800 hover:bg-slate-700 rounded-lg transition-colors"
          >
            Close Overview
          </button>
        </div>
      </div>
    </div>
  );
};
