import React from 'react';
import { CheckCircle2, Info } from 'lucide-react';

interface ModernToastProps {
  message: string | null;
}

export const ModernToast: React.FC<ModernToastProps> = ({ message }) => {
  if (!message) return null;

  return (
    <div className="fixed bottom-6 left-1/2 -translate-x-1/2 z-50 flex items-center gap-2.5 px-5 py-3 rounded-2xl bg-slate-900 text-white shadow-xl shadow-slate-900/25 border border-slate-800 text-sm font-semibold animate-in fade-in slide-in-from-bottom-5 duration-300">
      <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
      <span>{message}</span>
    </div>
  );
};
