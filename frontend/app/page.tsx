async function getBackendStatus() {
  try {
    const response = await fetch("http://localhost:8080/api/health", {
      cache: "no-store",
    });

    if (!response.ok) {
      return "backend indisponivel";
    }

    const data = (await response.json()) as { status?: string };
    return data.status ?? "sem resposta";
  } catch {
    return "backend nao iniciado";
  }
}

export default async function Home() {
  const backendStatus = await getBackendStatus();

  return (
    <main className="page">
      <section className="hero">
        <p className="eyebrow">Monorepo em andamento</p>
        <h1>Gerenciador de Portifolio</h1>
        <p className="description">
          Base inicial com backend em Spring Boot e frontend em Next.js pronta
          para evoluirmos as regras de negocio do desafio tecnico.
        </p>

        <div className="status-card">
          <span>Backend</span>
          <strong>{backendStatus}</strong>
        </div>
      </section>
    </main>
  );
}
