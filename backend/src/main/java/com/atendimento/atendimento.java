package com.atendimento;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class atendimento {

	private static final List<AtendimentoRecord> RECORDS = Collections.synchronizedList(new ArrayList<>());

	public static void main(String[] args) throws Exception {
		int port = Optional.ofNullable(System.getenv("ATENDIMENTO_PORT")).map(Integer::parseInt).orElse(8085);
		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/ping", new PingHandler());
		server.createContext("/status", new StatusHandler());
		server.createContext("/list", new ListHandler());
		server.createContext("/add", new AddHandler());
		server.createContext("/metrics", new MetricsHandler());

		server.setExecutor(Executors.newFixedThreadPool(4));
		seedSampleData();

		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(atendimento::generateSyntheticRecord, 10, 15, TimeUnit.SECONDS);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			server.stop(1);
			scheduler.shutdown();
			System.out.println("atendimento: server stopped");
		}));

		server.start();
		System.out.println("atendimento: server started on port " + port);
	}

	private static void seedSampleData() {
		for (int i = 1; i <= 5; i++) {
			RECORDS.add(new AtendimentoRecord(i, "Paciente " + i, "Profissional " + i, "Geral", Instant.now().toString()));
		}
	}

	private static void generateSyntheticRecord() {
		int id = RECORDS.size() + 1;
		AtendimentoRecord r = new AtendimentoRecord(id, "Paciente-Syn-" + id, "Profissional-Syn-" + ((id % 3) + 1), "Triagem", Instant.now().toString());
		RECORDS.add(r);
		System.out.println("atendimento: generated synthetic record " + r.id);
		if (RECORDS.size() > 200) {
			synchronized (RECORDS) {
				while (RECORDS.size() > 150) { RECORDS.remove(0); }
			}
		}
	}

	static class PingHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			String resp = "{\"status\":\"ok\",\"time\":\"" + Instant.now().toString() + "\"}";
			sendJson(exchange, resp);
		}
	}

	static class StatusHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			String resp = "{\"service\":\"atendimento-test\",\"records\":" + RECORDS.size() + ",\"time\":\"" + Instant.now().toString() + "\"}";
			sendJson(exchange, resp);
		}
	}

	static class ListHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			synchronized (RECORDS) {
				for (int i = 0; i < RECORDS.size(); i++) {
					sb.append(RECORDS.get(i).toJson());
					if (i < RECORDS.size() - 1) sb.append(',');
				}
			}
			sb.append(']');
			sendJson(exchange, sb.toString());
		}
	}

	static class AddHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			URI uri = exchange.getRequestURI();
			Map<String, String> q = QueryUtils.parseQuery(uri.getQuery());
			String paciente = q.getOrDefault("paciente", "Anon");
			String profissional = q.getOrDefault("profissional", "Unknown");
			String categoria = q.getOrDefault("categoria", "Geral");
			int id = RECORDS.size() + 1;
			AtendimentoRecord r = new AtendimentoRecord(id, paciente, profissional, categoria, Instant.now().toString());
			RECORDS.add(r);
			sendJson(exchange, r.toJson());
		}
	}

	static class MetricsHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange exchange) throws IOException {
			String resp = "records_total " + RECORDS.size() + "\n";
			sendPlain(exchange, resp);
		}
	}

	private static void sendJson(HttpExchange exchange, String body) throws IOException {
		byte[] out = body.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
		exchange.sendResponseHeaders(200, out.length);
		try (OutputStream os = exchange.getResponseBody()) { os.write(out); }
	}

	private static void sendPlain(HttpExchange exchange, String body) throws IOException {
		byte[] out = body.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
		exchange.sendResponseHeaders(200, out.length);
		try (OutputStream os = exchange.getResponseBody()) { os.write(out); }
	}

	static class AtendimentoRecord {
		final int id;
		final String paciente;
		final String profissional;
		final String categoria;
		final String timestamp;

		AtendimentoRecord(int id, String paciente, String profissional, String categoria, String timestamp) {
			this.id = id;
			this.paciente = paciente;
			this.profissional = profissional;
			this.categoria = categoria;
			this.timestamp = timestamp;
		}

		String toJson() {
			return "{" +
					"\"id\":" + id +
					",\"paciente\":\"" + escape(paciente) + "\"" +
					",\"profissional\":\"" + escape(profissional) + "\"" +
					",\"categoria\":\"" + escape(categoria) + "\"" +
					",\"timestamp\":\"" + escape(timestamp) + "\"" +
					"}";
		}

		private String escape(String s) {
			return s.replace("\\", "\\\\").replace("\"", "\\\"");
		}
	}

	static class QueryUtils {
		static Map<String, String> parseQuery(String query) {
			if (query == null || query.isEmpty()) return Collections.emptyMap();
			String[] parts = query.split("&");
			java.util.Map<String, String> m = new java.util.HashMap<>();
			for (String p : parts) {
				int i = p.indexOf('=');
				if (i > 0) {
					String k = decode(p.substring(0, i));
					String v = decode(p.substring(i + 1));
					m.put(k, v);
				} else {
					m.put(decode(p), "");
				}
			}
			return m;
		}

		private static String decode(String s) {
			try { return java.net.URLDecoder.decode(s, "UTF-8"); } catch (Exception e) { return s; }
		}
	}

}
