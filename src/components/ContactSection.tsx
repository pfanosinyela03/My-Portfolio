import React, { useState } from 'react';
import { Mail, MapPin, Send, CheckCircle2, Github, Linkedin, ExternalLink } from 'lucide-react';
import { PortfolioProfile } from '../data/portfolioData';

interface ContactSectionProps {
  profile: PortfolioProfile;
}

export const ContactSection: React.FC<ContactSectionProps> = ({ profile }) => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    projectType: 'Full-Stack Architecture',
    budget: '$10k - $25k',
    message: '',
  });

  const [submitted, setSubmitted] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name.trim() || !formData.email.trim() || !formData.message.trim()) {
      setError('Please provide your name, email, and a brief project overview.');
      return;
    }
    if (!formData.email.includes('@')) {
      setError('Please provide a valid email address.');
      return;
    }

    setError(null);
    setSubmitted(true);
  };

  const projectTypes = [
    'Full-Stack Architecture',
    'Cloud Systems & Scaling',
    'Mobile & Design Systems',
    'Fractional CTO / Advisory',
  ];

  return (
    <section id="contact" className="py-20 border-b border-slate-800/60 bg-slate-950/80">
      <div className="max-w-7xl mx-auto px-6">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12">
          {/* Left Column: Direct Inquiries Info */}
          <div className="lg:col-span-5 space-y-6">
            <div className="text-xs font-semibold uppercase tracking-wider text-blue-400">
              Initiate Collaboration
            </div>
            <h2 className="text-2xl sm:text-3xl lg:text-4xl font-bold tracking-tight text-white text-balance">
              Let's engineer something remarkable together.
            </h2>
            <p className="text-sm sm:text-base text-slate-300 leading-relaxed">
              Whether you need to architect a new high-throughput platform, migrate legacy infrastructure, or bring a polished digital product to life, my inbox is always open.
            </p>

            <div className="space-y-4 pt-4">
              <a
                href={`mailto:${profile.email}`}
                className="flex items-center gap-3 p-4 rounded-xl bg-slate-900 border border-slate-800 hover:border-blue-500/50 hover:bg-slate-850 transition-all text-xs group"
              >
                <div className="p-2.5 rounded-lg bg-blue-600/10 text-blue-400">
                  <Mail className="w-5 h-5" />
                </div>
                <div>
                  <div className="text-slate-400">Direct Email</div>
                  <div className="font-semibold text-white group-hover:text-blue-400 transition-colors">
                    {profile.email}
                  </div>
                </div>
              </a>

              <div className="flex items-center gap-3 p-4 rounded-xl bg-slate-900 border border-slate-800 text-xs">
                <div className="p-2.5 rounded-lg bg-emerald-600/10 text-emerald-400">
                  <MapPin className="w-5 h-5" />
                </div>
                <div>
                  <div className="text-slate-400">Location Base</div>
                  <div className="font-semibold text-white">{profile.location}</div>
                </div>
              </div>
            </div>

            <div className="pt-2 flex items-center gap-4 text-xs font-medium text-slate-400">
              <a
                href="https://github.com"
                target="_blank"
                rel="noreferrer"
                className="flex items-center gap-1.5 hover:text-white transition-colors"
              >
                <Github className="w-4 h-4" />
                <span>GitHub</span>
              </a>
              <span aria-hidden="true">·</span>
              <a
                href="https://linkedin.com"
                target="_blank"
                rel="noreferrer"
                className="flex items-center gap-1.5 hover:text-white transition-colors"
              >
                <Linkedin className="w-4 h-4" />
                <span>LinkedIn</span>
              </a>
              <span aria-hidden="true">·</span>
              <a
                href="https://x.com"
                target="_blank"
                rel="noreferrer"
                className="flex items-center gap-1.5 hover:text-white transition-colors"
              >
                <ExternalLink className="w-4 h-4" />
                <span>X / Twitter</span>
              </a>
            </div>
          </div>

          {/* Right Column: Interactive Lead Capture Form */}
          <div className="lg:col-span-7">
            <div className="p-6 sm:p-8 rounded-2xl border border-slate-800/80 bg-slate-900/60 shadow-xl">
              {submitted ? (
                <div className="py-12 text-center space-y-4">
                  <div className="inline-flex p-3 rounded-full bg-emerald-500/10 text-emerald-400 mb-2">
                    <CheckCircle2 className="w-10 h-10" />
                  </div>
                  <h3 className="text-xl font-bold text-white">Inquiry Dispatched Successfully</h3>
                  <p className="text-sm text-slate-300 max-w-md mx-auto leading-relaxed">
                    Thank you, {formData.name}. Your message has been received. I will review your requirements and respond directly to <span className="text-blue-400 font-medium">{formData.email}</span> within 24 hours.
                  </p>
                  <button
                    onClick={() => {
                      setSubmitted(false);
                      setFormData({
                        name: '',
                        email: '',
                        projectType: 'Full-Stack Architecture',
                        budget: '$10k - $25k',
                        message: '',
                      });
                    }}
                    className="mt-4 px-5 py-2 text-xs font-semibold text-white bg-slate-800 hover:bg-slate-700 rounded-lg transition-colors"
                  >
                    Send Another Note
                  </button>
                </div>
              ) : (
                <form onSubmit={handleSubmit} className="space-y-5">
                  <h3 className="text-lg font-bold text-white mb-2">
                    Start a Project Conversation
                  </h3>

                  {error && (
                    <div className="p-3 rounded-lg bg-rose-950/60 border border-rose-800/60 text-xs text-rose-300">
                      {error}
                    </div>
                  )}

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <div className="space-y-1.5">
                      <label className="text-xs font-medium text-slate-300">Your Name</label>
                      <input
                        type="text"
                        placeholder="e.g. Sarah Jenkins"
                        value={formData.name}
                        onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                        className="w-full px-3.5 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-sm text-white focus:outline-none focus:border-blue-500 transition-colors"
                        required
                      />
                    </div>
                    <div className="space-y-1.5">
                      <label className="text-xs font-medium text-slate-300">Email Address</label>
                      <input
                        type="email"
                        placeholder="name@company.com"
                        value={formData.email}
                        onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                        className="w-full px-3.5 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-sm text-white focus:outline-none focus:border-blue-500 transition-colors"
                        required
                      />
                    </div>
                  </div>

                  <div className="space-y-1.5">
                    <label className="text-xs font-medium text-slate-300">Project Domain / Scope</label>
                    <select
                      value={formData.projectType}
                      onChange={(e) => setFormData({ ...formData, projectType: e.target.value })}
                      className="w-full px-3.5 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-sm text-white focus:outline-none focus:border-blue-500 transition-colors"
                    >
                      {projectTypes.map((pt) => (
                        <option key={pt} value={pt}>
                          {pt}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="space-y-1.5">
                    <label className="text-xs font-medium text-slate-300">Project Brief & Deliverables</label>
                    <textarea
                      rows={4}
                      placeholder="Share high-level timelines, goals, and technical requirements..."
                      value={formData.message}
                      onChange={(e) => setFormData({ ...formData, message: e.target.value })}
                      className="w-full px-3.5 py-2.5 bg-slate-950 border border-slate-800 rounded-lg text-sm text-white focus:outline-none focus:border-blue-500 transition-colors resize-none"
                      required
                    />
                  </div>

                  <button
                    type="submit"
                    className="w-full py-3 px-6 text-sm font-semibold text-white bg-blue-600 hover:bg-blue-500 rounded-lg shadow-md shadow-blue-600/25 active:scale-98 transition-all flex items-center justify-center gap-2"
                  >
                    <Send className="w-4 h-4" />
                    <span>Submit Project Inquiry</span>
                  </button>
                </form>
              )}
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
