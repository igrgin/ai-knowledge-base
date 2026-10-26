import { StrictMode, useEffect, useState } from 'react';
import { createRoot } from 'react-dom/client';
import { Button } from './components/ui/button';
import './style.css';
function App() {
  const [status, setStatus] = useState<'checking' | 'ready' | 'unavailable'>('checking');
  async function check() {
    setStatus('checking');
    try {
      const response = await fetch('/actuator/health/readiness', { signal: AbortSignal.timeout(6000), cache: 'no-store' });
      const health = await response.json();
      setStatus(response.ok && health.status === 'UP' ? 'ready' : 'unavailable');
    } catch { setStatus('unavailable'); }
  }
  useEffect(() => { void check(); }, []);
  return <main className="mx-auto max-w-3xl px-6 py-16"><p className="text-sm font-medium uppercase tracking-widest text-slate-500">Local workspace</p>
    <h1 className="mt-4 text-4xl font-semibold tracking-tight">AI knowledge base</h1>
    <p className="mt-4 max-w-xl text-lg text-slate-600">A place to learn from your PDFs, with answers you can trace to the source.</p>
    <section className="mt-10 rounded-xl border border-slate-200 bg-white p-6 shadow-sm" aria-labelledby="runtime-title">
      <h2 id="runtime-title" className="text-lg font-semibold">Workspace readiness</h2>
      <p role="status" className="my-4 text-slate-600">{status === 'checking' ? 'Checking database connection…' : status === 'ready' ? 'Database connected. Workspace runtime is ready.' : 'Database unavailable. Start the local database and check again.'}</p>
      <Button onClick={() => void check()} disabled={status === 'checking'}>Check again</Button>
    </section>
    <p className="mt-6 text-sm text-slate-500">This first slice establishes the local runtime. Projects, PDF uploads and chat are coming in later slices.</p>
  </main>;
}
createRoot(document.getElementById('root')!).render(<StrictMode><App /></StrictMode>);
