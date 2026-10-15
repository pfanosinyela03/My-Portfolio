import React, { useState } from 'react';
import { Mail, Send, CheckCircle2, Linkedin, Instagram, Github, MessageSquare } from 'lucide-react';
import { pfanoProfile } from '../data/pfanoData';

interface ModernContactProps {
  onShowToast: (message: string) => void;
}

export const ModernContact: React.FC<ModernContactProps> = ({ onShowToast }) => {
  const directEmailHref = `mailto:${pfanoProfile.email}?subject=${encodeURIComponent('Portfolio enquiry')}&body=${encodeURIComponent('Hi Pfano,\n\n')}`;
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    message: '',
  });
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.name.trim() || !formData.email.trim() || !formData.message.trim()) {
      onShowToast('Please fill in all required fields.');
      return;
    }

    setIsSubmitting(true);

    // EmailJS is loaded by index.html, matching the original portfolio setup.
    const win = window as any;
    if (!win.emailjs) {
      console.error('EmailJS failed to load.');
      onShowToast('Email service is unavailable. Please try again.');
      setIsSubmitting(false);
      return;
    }

    try {
      win.emailjs.init({ publicKey: 'NcLdMw66z9IL0N_S_' });
      win.emailjs
        .sendForm('service_t5ie62i', 'template_k8xxrp8', e.currentTarget)
        .then(() => {
          onShowToast('Message sent successfully!');
          setFormData({ name: '', email: '', message: '' });
        })
        .catch((err: unknown) => {
          console.error('EmailJS error:', err);
          onShowToast('Something went wrong. Please try again.');
        })
        .finally(() => {
          setIsSubmitting(false);
        });
    } catch (err) {
      console.error('EmailJS error:', err);
      onShowToast('Something went wrong. Please try again.');
      setIsSubmitting(false);
    }
  };

  return (
    <section id="contact" className="py-20 border-b border-slate-200/80 bg-white">
      <div className="max-w-6xl mx-auto px-6">
        {/* Section Header */}
        <div className="text-center max-w-2xl mx-auto mb-14">
          <div className="text-xs font-bold uppercase tracking-wider text-blue-600 mb-2">
            Get in touch
          </div>
          <h2 className="text-3xl sm:text-4xl font-bold tracking-tight text-slate-900">
            Contact Me
          </h2>
          <div className="w-16 h-1 bg-blue-600 mx-auto mt-3 rounded-full" />
          <p className="mt-4 text-base text-slate-600 leading-relaxed">
            I am always open to discussing new projects, creative ideas, or opportunities to collaborate.
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-start">
          {/* Left Column: Direct Info Cards */}
          <div className="lg:col-span-5 space-y-4">
            <h3 className="text-lg font-bold text-slate-900 mb-2">
              Let's talk about tech & opportunities
            </h3>

            <a
              href={directEmailHref}
              aria-label={`Send an email to ${pfanoProfile.email}`}
              title="Open your email app"
              className="flex items-center gap-4 p-4 rounded-2xl bg-slate-50 border border-slate-200 hover:border-blue-300 hover:bg-blue-50/40 transition-all group"
            >
              <div className="p-3 rounded-xl bg-blue-100 text-blue-600 group-hover:scale-105 transition-transform">
                <Mail className="w-5 h-5" />
              </div>
              <div>
                <div className="text-xs font-semibold text-slate-400">Direct Email</div>
                <div className="text-sm font-bold text-slate-900 group-hover:text-blue-600 transition-colors">
                  {pfanoProfile.email}
                </div>
              </div>
            </a>

            <div className="p-5 rounded-2xl bg-slate-50 border border-slate-200 space-y-3">
              <div className="text-xs font-bold uppercase tracking-wider text-slate-500">
                Social Profiles
              </div>
              <div className="grid grid-cols-3 gap-2">
                <a
                  href={pfanoProfile.linkedin}
                  target="_blank"
                  rel="noreferrer"
                  className="p-3 rounded-xl bg-white border border-slate-200 text-center hover:border-blue-400 hover:shadow-xs transition-all group"
                >
                  <Linkedin className="w-5 h-5 mx-auto text-blue-600 mb-1" />
                  <span className="text-[11px] font-semibold text-slate-700 block">LinkedIn</span>
                </a>
                <a
                  href={pfanoProfile.github}
                  target="_blank"
                  rel="noreferrer"
                  className="p-3 rounded-xl bg-white border border-slate-200 text-center hover:border-slate-800 hover:shadow-xs transition-all group"
                >
                  <Github className="w-5 h-5 mx-auto text-slate-800 mb-1" />
                  <span className="text-[11px] font-semibold text-slate-700 block">GitHub</span>
                </a>
                <a
                  href={pfanoProfile.instagram}
                  target="_blank"
                  rel="noreferrer"
                  className="p-3 rounded-xl bg-white border border-slate-200 text-center hover:border-pink-400 hover:shadow-xs transition-all group"
                >
                  <Instagram className="w-5 h-5 mx-auto text-pink-600 mb-1" />
                  <span className="text-[11px] font-semibold text-slate-700 block">Instagram</span>
                </a>
              </div>
            </div>
          </div>

          {/* Right Column: Modern Light Contact Form */}
          <div className="lg:col-span-7">
            <div className="p-6 sm:p-8 rounded-3xl bg-slate-50 border border-slate-200/90 shadow-sm">
              <form onSubmit={handleSubmit} className="space-y-4">
                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-slate-700">Your Name</label>
                  <input
                    type="text"
                    name="name"
                    placeholder="e.g. Tony Stark"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    className="w-full px-4 py-3 bg-white border border-slate-200 rounded-xl text-sm text-slate-900 placeholder-slate-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all"
                    required
                  />
                </div>

                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-slate-700">Your Email</label>
                  <input
                    type="email"
                    name="email"
                    placeholder="stark@gmail.com"
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    className="w-full px-4 py-3 bg-white border border-slate-200 rounded-xl text-sm text-slate-900 placeholder-slate-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all"
                    required
                  />
                </div>

                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-slate-700">Your Message</label>
                  <textarea
                    rows={4}
                    name="message"
                    placeholder="Tell me about your project, idea, or questions..."
                    value={formData.message}
                    onChange={(e) => setFormData({ ...formData, message: e.target.value })}
                    className="w-full px-4 py-3 bg-white border border-slate-200 rounded-xl text-sm text-slate-900 placeholder-slate-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 transition-all resize-none"
                    required
                  />
                </div>

                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="w-full py-3.5 px-6 text-sm font-bold text-white bg-blue-600 hover:bg-blue-700 active:scale-98 rounded-xl shadow-md shadow-blue-600/20 transition-all flex items-center justify-center gap-2 cursor-pointer disabled:opacity-50"
                >
                  <Send className="w-4 h-4" />
                  <span>{isSubmitting ? 'Sending Message...' : 'Send Message'}</span>
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
