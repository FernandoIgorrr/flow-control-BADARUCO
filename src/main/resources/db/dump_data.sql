--
-- PostgreSQL database dump
--

\restrict CGZ4yMTqvDrbWANdeYxiKIoSNFHpxb4BbldDXyOjwBxekpzzF4aN4UP55iLXQuM

-- Dumped from database version 18.1
-- Dumped by pg_dump version 18.0

-- Started on 2026-01-27 13:37:15

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 5154 (class 0 OID 42101)
-- Dependencies: 219
-- Data for Name: city; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.city (id, name) FROM stdin;
1	Acari (RN)
2	Açu (RN)
3	Afonso Bezerra (RN)
4	Água Nova (RN)
5	Alexandria (RN)
6	Almino Afonso (RN)
7	Alto do Rodrigues (RN)
8	Angicos (RN)
9	Antônio Martins (RN)
10	Apodi (RN)
11	Areia Branca (RN)
12	Arês (RN)
13	Campo Grande (RN)
14	Baía Formosa (RN)
15	Baraúna (RN)
16	Barcelona (RN)
17	Bento Fernandes (RN)
18	Bodó (RN)
19	Bom Jesus (RN)
20	Brejinho (RN)
21	Caiçara do Norte (RN)
22	Caiçara do Rio do Vento (RN)
23	Caicó (RN)
24	Campo Redondo (RN)
25	Canguaretama (RN)
26	Caraúbas (RN)
27	Carnaúba dos Dantas (RN)
28	Carnaubais (RN)
29	Ceará-Mirim (RN)
30	Cerro Corá (RN)
31	Coronel Ezequiel (RN)
32	Coronel João Pessoa (RN)
33	Cruzeta (RN)
34	Currais Novos (RN)
35	Doutor Severiano (RN)
36	Parnamirim (RN)
37	Encanto (RN)
38	Equador (RN)
39	Espírito Santo (RN)
40	Extremoz (RN)
41	Felipe Guerra (RN)
42	Fernando Pedroza (RN)
43	Florânia (RN)
44	Francisco Dantas (RN)
45	Frutuoso Gomes (RN)
46	Galinhos (RN)
47	Goianinha (RN)
48	Governador Dix-Sept Rosado (RN)
49	Grossos (RN)
50	Guamaré (RN)
51	Ielmo Marinho (RN)
52	Ipanguaçu (RN)
53	Ipueira (RN)
54	Itajá (RN)
55	Itaú (RN)
56	Jaçanã (RN)
57	Jandaíra (RN)
58	Janduís (RN)
59	Januário Cicco (RN)
60	Japi (RN)
61	Jardim de Angicos (RN)
62	Jardim de Piranhas (RN)
63	Jardim do Seridó (RN)
64	João Câmara (RN)
65	João Dias (RN)
66	José da Penha (RN)
67	Jucurutu (RN)
68	Jundiá (RN)
69	Lagoa d'Anta (RN)
70	Lagoa de Pedras (RN)
71	Lagoa de Velhos (RN)
72	Lagoa Nova (RN)
73	Lagoa Salgada (RN)
74	Lajes (RN)
75	Lajes Pintadas (RN)
76	Lucrécia (RN)
77	Luís Gomes (RN)
78	Macaíba (RN)
79	Macau (RN)
80	Major Sales (RN)
81	Marcelino Vieira (RN)
82	Martins (RN)
83	Maxaranguape (RN)
84	Messias Targino (RN)
85	Montanhas (RN)
86	Monte Alegre (RN)
87	Monte das Gameleiras (RN)
88	Mossoró (RN)
89	Natal (RN)
90	Nísia Floresta (RN)
91	Nova Cruz (RN)
92	Olho d'Água do Borges (RN)
93	Ouro Branco (RN)
94	Paraná (RN)
95	Paraú (RN)
96	Parazinho (RN)
97	Parelhas (RN)
98	Rio do Fogo (RN)
99	Passa e Fica (RN)
100	Passagem (RN)
101	Patu (RN)
102	Santa Maria (RN)
103	Pau dos Ferros (RN)
104	Pedra Grande (RN)
105	Pedra Preta (RN)
106	Pedro Avelino (RN)
107	Pedro Velho (RN)
108	Pendências (RN)
109	Pilões (RN)
110	Poço Branco (RN)
111	Portalegre (RN)
112	Porto do Mangue (RN)
113	Serra Caiada (RN)
114	Pureza (RN)
115	Rafael Fernandes (RN)
116	Rafael Godeiro (RN)
117	Riacho da Cruz (RN)
118	Riacho de Santana (RN)
119	Riachuelo (RN)
120	Rodolfo Fernandes (RN)
121	Tibau (RN)
122	Ruy Barbosa (RN)
123	Santa Cruz (RN)
124	Santana do Matos (RN)
125	Santana do Seridó (RN)
126	Santo Antônio (RN)
127	São Bento do Norte (RN)
128	São Bento do Trairí (RN)
129	São Fernando (RN)
130	São Francisco do Oeste (RN)
131	São Gonçalo do Amarante (RN)
132	São João do Sabugi (RN)
133	São José de Mipibu (RN)
134	São José do Campestre (RN)
135	São José do Seridó (RN)
136	São Miguel (RN)
137	São Miguel do Gostoso (RN)
138	São Paulo do Potengi (RN)
139	São Pedro (RN)
140	São Rafael (RN)
141	São Tomé (RN)
142	São Vicente (RN)
143	Senador Elói de Souza (RN)
144	Senador Georgino Avelino (RN)
145	Serra de São Bento (RN)
146	Serra do Mel (RN)
147	Serra Negra do Norte (RN)
148	Serrinha (RN)
149	Serrinha dos Pintos (RN)
150	Severiano Melo (RN)
151	Sítio Novo (RN)
152	Taboleiro Grande (RN)
153	Taipu (RN)
154	Tangará (RN)
155	Tenente Ananias (RN)
156	Tenente Laurentino Cruz (RN)
157	Tibau do Sul (RN)
158	Timbaúba dos Batistas (RN)
159	Touros (RN)
160	Triunfo Potiguar (RN)
161	Umarizal (RN)
162	Upanema (RN)
163	Várzea (RN)
164	Venha-Ver (RN)
165	Vera Cruz (RN)
166	Viçosa (RN)
167	Vila Flor (RN)
168	Água Branca (PB)
169	Aguiar (PB)
170	Alagoa Grande (PB)
171	Alagoa Nova (PB)
172	Alagoinha (PB)
173	Alcantil (PB)
174	Algodão de Jandaíra (PB)
175	Alhandra (PB)
176	São João do Rio do Peixe (PB)
177	Amparo (PB)
178	Aparecida (PB)
179	Araçagi (PB)
180	Arara (PB)
181	Araruna (PB)
182	Areia (PB)
183	Areia de Baraúnas (PB)
184	Areial (PB)
185	Aroeiras (PB)
186	Assunção (PB)
187	Baía da Traição (PB)
188	Bananeiras (PB)
189	Baraúna (PB)
190	Barra de Santana (PB)
191	Barra de Santa Rosa (PB)
192	Barra de São Miguel (PB)
193	Bayeux (PB)
194	Belém (PB)
195	Belém do Brejo do Cruz (PB)
196	Bernardino Batista (PB)
197	Boa Ventura (PB)
198	Boa Vista (PB)
199	Bom Jesus (PB)
200	Bom Sucesso (PB)
201	Bonito de Santa Fé (PB)
202	Boqueirão (PB)
203	Igaracy (PB)
204	Borborema (PB)
205	Brejo do Cruz (PB)
206	Brejo dos Santos (PB)
207	Caaporã (PB)
208	Cabaceiras (PB)
209	Cabedelo (PB)
210	Cachoeira dos Índios (PB)
211	Cacimba de Areia (PB)
212	Cacimba de Dentro (PB)
213	Cacimbas (PB)
214	Caiçara (PB)
215	Cajazeiras (PB)
216	Cajazeirinhas (PB)
217	Caldas Brandão (PB)
218	Camalaú (PB)
219	Campina Grande (PB)
220	Capim (PB)
221	Caraúbas (PB)
222	Carrapateira (PB)
223	Casserengue (PB)
224	Catingueira (PB)
225	Catolé do Rocha (PB)
226	Caturité (PB)
227	Conceição (PB)
228	Condado (PB)
229	Conde (PB)
230	Congo (PB)
231	Coremas (PB)
232	Coxixola (PB)
233	Cruz do Espírito Santo (PB)
234	Cubati (PB)
235	Cuité (PB)
236	Cuitegi (PB)
237	Cuité de Mamanguape (PB)
238	Curral de Cima (PB)
239	Curral Velho (PB)
240	Damião (PB)
241	Desterro (PB)
242	Vista Serrana (PB)
243	Diamante (PB)
244	Dona Inês (PB)
245	Duas Estradas (PB)
246	Emas (PB)
247	Esperança (PB)
248	Fagundes (PB)
249	Frei Martinho (PB)
250	Gado Bravo (PB)
251	Guarabira (PB)
252	Gurinhém (PB)
253	Gurjão (PB)
254	Ibiara (PB)
255	Imaculada (PB)
256	Ingá (PB)
257	Itabaiana (PB)
258	Itaporanga (PB)
259	Itapororoca (PB)
260	Itatuba (PB)
261	Jacaraú (PB)
262	Jericó (PB)
263	João Pessoa (PB)
264	Juarez Távora (PB)
265	Juazeirinho (PB)
266	Junco do Seridó (PB)
267	Juripiranga (PB)
268	Juru (PB)
269	Lagoa (PB)
270	Lagoa de Dentro (PB)
271	Lagoa Seca (PB)
272	Lastro (PB)
273	Livramento (PB)
274	Logradouro (PB)
275	Lucena (PB)
276	Mãe d'Água (PB)
277	Malta (PB)
278	Mamanguape (PB)
279	Manaíra (PB)
280	Marcação (PB)
281	Mari (PB)
282	Marizópolis (PB)
283	Massaranduba (PB)
284	Mataraca (PB)
285	Matinhas (PB)
286	Mato Grosso (PB)
287	Maturéia (PB)
288	Mogeiro (PB)
289	Montadas (PB)
290	Monte Horebe (PB)
291	Monteiro (PB)
292	Mulungu (PB)
293	Natuba (PB)
294	Nazarezinho (PB)
295	Nova Floresta (PB)
296	Nova Olinda (PB)
297	Nova Palmeira (PB)
298	Olho d'Água (PB)
299	Olivedos (PB)
300	Ouro Velho (PB)
301	Parari (PB)
302	Passagem (PB)
303	Patos (PB)
304	Paulista (PB)
305	Pedra Branca (PB)
306	Pedra Lavrada (PB)
307	Pedras de Fogo (PB)
308	Piancó (PB)
309	Picuí (PB)
310	Pilar (PB)
311	Pilões (PB)
312	Pilõezinhos (PB)
313	Pirpirituba (PB)
314	Pitimbu (PB)
315	Pocinhos (PB)
316	Poço Dantas (PB)
317	Poço de José de Moura (PB)
318	Pombal (PB)
319	Prata (PB)
320	Princesa Isabel (PB)
321	Puxinanã (PB)
322	Queimadas (PB)
323	Quixaba (PB)
324	Remígio (PB)
325	Pedro Régis (PB)
326	Riachão (PB)
327	Riachão do Bacamarte (PB)
328	Riachão do Poço (PB)
329	Riacho de Santo Antônio (PB)
330	Riacho dos Cavalos (PB)
331	Rio Tinto (PB)
332	Salgadinho (PB)
333	Salgado de São Félix (PB)
334	Santa Cecília (PB)
335	Santa Cruz (PB)
336	Santa Helena (PB)
337	Santa Inês (PB)
338	Santa Luzia (PB)
339	Santana de Mangueira (PB)
340	Santana dos Garrotes (PB)
341	Joca Claudino (PB)
342	Santa Rita (PB)
343	Santa Teresinha (PB)
344	Santo André (PB)
345	São Bento (PB)
346	São Bentinho (PB)
347	São Domingos do Cariri (PB)
348	São Domingos (PB)
349	São Francisco (PB)
350	São João do Cariri (PB)
351	São João do Tigre (PB)
352	São José da Lagoa Tapada (PB)
353	São José de Caiana (PB)
354	São José de Espinharas (PB)
355	São José dos Ramos (PB)
356	São José de Piranhas (PB)
357	São José de Princesa (PB)
358	São José do Bonfim (PB)
359	São José do Brejo do Cruz (PB)
360	São José do Sabugi (PB)
361	São José dos Cordeiros (PB)
362	São Mamede (PB)
363	São Miguel de Taipu (PB)
364	São Sebastião de Lagoa de Roça (PB)
365	São Sebastião do Umbuzeiro (PB)
366	Sapé (PB)
367	São Vicente do Seridó (PB)
368	Serra Branca (PB)
369	Serra da Raiz (PB)
370	Serra Grande (PB)
371	Serra Redonda (PB)
372	Serraria (PB)
373	Sertãozinho (PB)
374	Sobrado (PB)
375	Solânea (PB)
376	Soledade (PB)
377	Sossêgo (PB)
378	Sousa (PB)
379	Sumé (PB)
380	Tacima (PB)
381	Taperoá (PB)
382	Tavares (PB)
383	Teixeira (PB)
384	Tenório (PB)
385	Triunfo (PB)
386	Uiraúna (PB)
387	Umbuzeiro (PB)
388	Várzea (PB)
389	Vieirópolis (PB)
390	Zabelê (PB)
391	Abreu e Lima (PE)
392	Afogados da Ingazeira (PE)
393	Afrânio (PE)
394	Agrestina (PE)
395	Água Preta (PE)
396	Águas Belas (PE)
397	Alagoinha (PE)
398	Aliança (PE)
399	Altinho (PE)
400	Amaraji (PE)
401	Angelim (PE)
402	Araçoiaba (PE)
403	Araripina (PE)
404	Arcoverde (PE)
405	Barra de Guabiraba (PE)
406	Barreiros (PE)
407	Belém de Maria (PE)
408	Belém do São Francisco (PE)
409	Belo Jardim (PE)
410	Betânia (PE)
411	Bezerros (PE)
412	Bodocó (PE)
413	Bom Conselho (PE)
414	Bom Jardim (PE)
415	Bonito (PE)
416	Brejão (PE)
417	Brejinho (PE)
418	Brejo da Madre de Deus (PE)
419	Buenos Aires (PE)
420	Buíque (PE)
421	Cabo de Santo Agostinho (PE)
422	Cabrobó (PE)
423	Cachoeirinha (PE)
424	Caetés (PE)
425	Calçado (PE)
426	Calumbi (PE)
427	Camaragibe (PE)
428	Camocim de São Félix (PE)
429	Camutanga (PE)
430	Canhotinho (PE)
431	Capoeiras (PE)
432	Carnaíba (PE)
433	Carnaubeira da Penha (PE)
434	Carpina (PE)
435	Caruaru (PE)
436	Casinhas (PE)
437	Catende (PE)
438	Cedro (PE)
439	Chã de Alegria (PE)
440	Chã Grande (PE)
441	Condado (PE)
442	Correntes (PE)
443	Cortês (PE)
444	Cumaru (PE)
445	Cupira (PE)
446	Custódia (PE)
447	Dormentes (PE)
448	Escada (PE)
449	Exu (PE)
450	Feira Nova (PE)
451	Fernando de Noronha (PE)
452	Ferreiros (PE)
453	Flores (PE)
454	Floresta (PE)
455	Frei Miguelinho (PE)
456	Gameleira (PE)
457	Garanhuns (PE)
458	Glória do Goitá (PE)
459	Goiana (PE)
460	Granito (PE)
461	Gravatá (PE)
462	Iati (PE)
463	Ibimirim (PE)
464	Ibirajuba (PE)
465	Igarassu (PE)
466	Iguaracy (PE)
467	Inajá (PE)
468	Ingazeira (PE)
469	Ipojuca (PE)
470	Ipubi (PE)
471	Itacuruba (PE)
472	Itaíba (PE)
473	Ilha de Itamaracá (PE)
474	Itambé (PE)
475	Itapetim (PE)
476	Itapissuma (PE)
477	Itaquitinga (PE)
478	Jaboatão dos Guararapes (PE)
479	Jaqueira (PE)
480	Jataúba (PE)
481	Jatobá (PE)
482	João Alfredo (PE)
483	Joaquim Nabuco (PE)
484	Jucati (PE)
485	Jupi (PE)
486	Jurema (PE)
487	Lagoa do Carro (PE)
488	Lagoa de Itaenga (PE)
489	Lagoa do Ouro (PE)
490	Lagoa dos Gatos (PE)
491	Lagoa Grande (PE)
492	Lajedo (PE)
493	Limoeiro (PE)
494	Macaparana (PE)
495	Machados (PE)
496	Manari (PE)
497	Maraial (PE)
498	Mirandiba (PE)
499	Moreno (PE)
500	Nazaré da Mata (PE)
501	Olinda (PE)
502	Orobó (PE)
503	Orocó (PE)
504	Ouricuri (PE)
505	Palmares (PE)
506	Palmeirina (PE)
507	Panelas (PE)
508	Paranatama (PE)
509	Parnamirim (PE)
510	Passira (PE)
511	Paudalho (PE)
512	Paulista (PE)
513	Pedra (PE)
514	Pesqueira (PE)
515	Petrolândia (PE)
516	Petrolina (PE)
517	Poção (PE)
518	Pombos (PE)
519	Primavera (PE)
520	Quipapá (PE)
521	Quixaba (PE)
522	Recife (PE)
523	Riacho das Almas (PE)
524	Ribeirão (PE)
525	Rio Formoso (PE)
526	Sairé (PE)
527	Salgadinho (PE)
528	Salgueiro (PE)
529	Saloá (PE)
530	Sanharó (PE)
531	Santa Cruz (PE)
532	Santa Cruz da Baixa Verde (PE)
533	Santa Cruz do Capibaribe (PE)
534	Santa Filomena (PE)
535	Santa Maria da Boa Vista (PE)
536	Santa Maria do Cambucá (PE)
537	Santa Terezinha (PE)
538	São Benedito do Sul (PE)
539	São Bento do Una (PE)
540	São Caitano (PE)
541	São João (PE)
542	São Joaquim do Monte (PE)
543	São José da Coroa Grande (PE)
544	São José do Belmonte (PE)
545	São José do Egito (PE)
546	São Lourenço da Mata (PE)
547	São Vicente Férrer (PE)
548	Serra Talhada (PE)
549	Serrita (PE)
550	Sertânia (PE)
551	Sirinhaém (PE)
552	Moreilândia (PE)
553	Solidão (PE)
554	Surubim (PE)
555	Tabira (PE)
556	Tacaimbó (PE)
557	Tacaratu (PE)
558	Tamandaré (PE)
559	Taquaritinga do Norte (PE)
560	Terezinha (PE)
561	Terra Nova (PE)
562	Timbaúba (PE)
563	Toritama (PE)
564	Tracunhaém (PE)
565	Trindade (PE)
566	Triunfo (PE)
567	Tupanatinga (PE)
568	Tuparetama (PE)
569	Venturosa (PE)
570	Verdejante (PE)
571	Vertente do Lério (PE)
572	Vertentes (PE)
573	Vicência (PE)
574	Vitória de Santo Antão (PE)
575	Xexéu (PE)
576	Abaiara (CE)
577	Acarape (CE)
578	Acaraú (CE)
579	Acopiara (CE)
580	Aiuaba (CE)
581	Alcântaras (CE)
582	Altaneira (CE)
583	Alto Santo (CE)
584	Amontada (CE)
585	Antonina do Norte (CE)
586	Apuiarés (CE)
587	Aquiraz (CE)
588	Aracati (CE)
589	Aracoiaba (CE)
590	Ararendá (CE)
591	Araripe (CE)
592	Aratuba (CE)
593	Arneiroz (CE)
594	Assaré (CE)
595	Aurora (CE)
596	Baixio (CE)
597	Banabuiú (CE)
598	Barbalha (CE)
599	Barreira (CE)
600	Barro (CE)
601	Barroquinha (CE)
602	Baturité (CE)
603	Beberibe (CE)
604	Bela Cruz (CE)
605	Boa Viagem (CE)
606	Brejo Santo (CE)
607	Camocim (CE)
608	Campos Sales (CE)
609	Canindé (CE)
610	Capistrano (CE)
611	Caridade (CE)
612	Cariré (CE)
613	Caririaçu (CE)
614	Cariús (CE)
615	Carnaubal (CE)
616	Cascavel (CE)
617	Catarina (CE)
618	Catunda (CE)
619	Caucaia (CE)
620	Cedro (CE)
621	Chaval (CE)
622	Choró (CE)
623	Chorozinho (CE)
624	Coreaú (CE)
625	Crateús (CE)
626	Crato (CE)
627	Croatá (CE)
628	Cruz (CE)
629	Deputado Irapuan Pinheiro (CE)
630	Ereré (CE)
631	Eusébio (CE)
632	Farias Brito (CE)
633	Forquilha (CE)
634	Fortaleza (CE)
635	Fortim (CE)
636	Frecheirinha (CE)
637	General Sampaio (CE)
638	Graça (CE)
639	Granja (CE)
640	Granjeiro (CE)
641	Groaíras (CE)
642	Guaiúba (CE)
643	Guaraciaba do Norte (CE)
644	Guaramiranga (CE)
645	Hidrolândia (CE)
646	Horizonte (CE)
647	Ibaretama (CE)
648	Ibiapina (CE)
649	Ibicuitinga (CE)
650	Icapuí (CE)
651	Icó (CE)
652	Iguatu (CE)
653	Independência (CE)
654	Ipaporanga (CE)
655	Ipaumirim (CE)
656	Ipu (CE)
657	Ipueiras (CE)
658	Iracema (CE)
659	Irauçuba (CE)
660	Itaiçaba (CE)
661	Itaitinga (CE)
662	Itapajé (CE)
663	Itapipoca (CE)
664	Itapiúna (CE)
665	Itarema (CE)
666	Itatira (CE)
667	Jaguaretama (CE)
668	Jaguaribara (CE)
669	Jaguaribe (CE)
670	Jaguaruana (CE)
671	Jardim (CE)
672	Jati (CE)
673	Jijoca de Jericoacoara (CE)
674	Juazeiro do Norte (CE)
675	Jucás (CE)
676	Lavras da Mangabeira (CE)
677	Limoeiro do Norte (CE)
678	Madalena (CE)
679	Maracanaú (CE)
680	Maranguape (CE)
681	Marco (CE)
682	Martinópole (CE)
683	Massapê (CE)
684	Mauriti (CE)
685	Meruoca (CE)
686	Milagres (CE)
687	Milhã (CE)
688	Miraíma (CE)
689	Missão Velha (CE)
690	Mombaça (CE)
691	Monsenhor Tabosa (CE)
692	Morada Nova (CE)
693	Moraújo (CE)
694	Morrinhos (CE)
695	Mucambo (CE)
696	Mulungu (CE)
697	Nova Olinda (CE)
698	Nova Russas (CE)
699	Novo Oriente (CE)
700	Ocara (CE)
701	Orós (CE)
702	Pacajus (CE)
703	Pacatuba (CE)
704	Pacoti (CE)
705	Pacujá (CE)
706	Palhano (CE)
707	Palmácia (CE)
708	Paracuru (CE)
709	Paraipaba (CE)
710	Parambu (CE)
711	Paramoti (CE)
712	Pedra Branca (CE)
713	Penaforte (CE)
714	Pentecoste (CE)
715	Pereiro (CE)
716	Pindoretama (CE)
717	Piquet Carneiro (CE)
718	Pires Ferreira (CE)
719	Poranga (CE)
720	Porteiras (CE)
721	Potengi (CE)
722	Potiretama (CE)
723	Quiterianópolis (CE)
724	Quixadá (CE)
725	Quixelô (CE)
726	Quixeramobim (CE)
727	Quixeré (CE)
728	Redenção (CE)
729	Reriutaba (CE)
730	Russas (CE)
731	Saboeiro (CE)
732	Salitre (CE)
733	Santana do Acaraú (CE)
734	Santana do Cariri (CE)
735	Santa Quitéria (CE)
736	São Benedito (CE)
737	São Gonçalo do Amarante (CE)
738	São João do Jaguaribe (CE)
739	São Luís do Curu (CE)
740	Senador Pompeu (CE)
741	Senador Sá (CE)
742	Sobral (CE)
743	Solonópole (CE)
744	Tabuleiro do Norte (CE)
745	Tamboril (CE)
746	Tarrafas (CE)
747	Tauá (CE)
748	Tejuçuoca (CE)
749	Tianguá (CE)
750	Trairi (CE)
751	Tururu (CE)
752	Ubajara (CE)
753	Umari (CE)
754	Umirim (CE)
755	Uruburetama (CE)
756	Uruoca (CE)
757	Varjota (CE)
758	Várzea Alegre (CE)
759	Viçosa do Ceará (CE)
\.


--
-- TOC entry 5164 (class 0 OID 42158)
-- Dependencies: 230
-- Data for Name: partner_role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.partner_role (id, name) FROM stdin;
1	Cliente
2	Fornecedor
\.


--
-- TOC entry 5157 (class 0 OID 42113)
-- Dependencies: 222
-- Data for Name: partner; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.partner (id, name, email, phone, city_id, role_id, created_at, deleted_at) FROM stdin;
e6d00214-7ec3-4cbd-9bae-7159e663549a	Rede Seridó	contato@redeserido.com	23423523525	23	1	2026-01-21 18:29:46.034613-03	\N
30357910-daa0-4275-948a-4bfd261a01bc	Rede mais	contato@redemais.com	23423523523	89	1	2026-01-21 18:30:18.366509-03	\N
ec43d468-f207-4407-8ea4-b220f269d14d	Fernando Igor	fernandoigor@gmail.com	34235235235	23	1	2026-01-21 18:30:43.59102-03	\N
3aaf5d05-fbce-406b-9430-212d2848be0f	Sítio acaru			132	2	2026-01-21 18:31:34.688174-03	\N
f8a79ac7-da44-4066-a7d4-0fef235967de	Roberto da Silva		34234234343	135	2	2026-01-21 18:32:22.079016-03	\N
15c4dc09-1102-40e0-8f49-4f3ec564079f	Assaí atacado	assai@gmail.com	72362736239	36	1	2026-01-21 18:33:45.725519-03	\N
9a0c946d-b19b-408e-af57-ac511a24d00b	José Oliveira		23452352352	33	1	2026-01-21 18:34:13.147074-03	\N
f2ee3c7a-1a55-4914-8722-9cc139ba910b	Aldinha		43534534534	72	2	2026-01-21 18:38:23.787536-03	\N
5823c324-8cb0-48c4-a671-4fc92c56b0f8	Feira			23	1	2026-01-22 15:32:52.550311-03	\N
0eb94b0a-aa94-4e96-ada5-77b072db5f5c	asdasd			1	1	2026-01-22 18:32:25.271153-03	\N
81cf5c65-50dd-45cf-a53b-5c484e93680b	Atacado whatever		12312412412	132	2	2026-01-23 15:34:13.664433-03	\N
5b515060-fa65-45ea-b31d-059d320f01d6	supermercado qualquer 			23	1	2026-01-26 14:44:03.284304-03	\N
8a881a8c-a55e-4f50-8496-300bc33259c5	SItio Qualquer			23	2	2026-01-26 14:45:08.685295-03	\N
\.


--
-- TOC entry 5156 (class 0 OID 42109)
-- Dependencies: 221
-- Data for Name: company_partner; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.company_partner (id, cnpj) FROM stdin;
e6d00214-7ec3-4cbd-9bae-7159e663549a	42409369000100
30357910-daa0-4275-948a-4bfd261a01bc	75528450000150
3aaf5d05-fbce-406b-9430-212d2848be0f	23097633000165
15c4dc09-1102-40e0-8f49-4f3ec564079f	64170075000101
81cf5c65-50dd-45cf-a53b-5c484e93680b	13582057000198
5b515060-fa65-45ea-b31d-059d320f01d6	\N
8a881a8c-a55e-4f50-8496-300bc33259c5	\N
\.


--
-- TOC entry 5159 (class 0 OID 42133)
-- Dependencies: 225
-- Data for Name: employee; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.employee (id, name) FROM stdin;
\.


--
-- TOC entry 5160 (class 0 OID 42140)
-- Dependencies: 226
-- Data for Name: employee_wage; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.employee_wage (id, wage, wage_change_date, employee_id) FROM stdin;
\.


--
-- TOC entry 5162 (class 0 OID 42148)
-- Dependencies: 228
-- Data for Name: measurement_unit; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.measurement_unit (id, name, plural_name, symbol, unit) FROM stdin;
1	Quilograma	Quilogramas	Kg	Peso
2	Litro	Litros	L	Volume
3	Metro Cúbico	Metros Cúbicos	m3	Volume
4	UNIDADEs	UNIDADES	UN	UNIDADE
\.


--
-- TOC entry 5158 (class 0 OID 42124)
-- Dependencies: 223
-- Data for Name: personal_partner; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.personal_partner (id, cpf) FROM stdin;
ec43d468-f207-4407-8ea4-b220f269d14d	01748801465
f8a79ac7-da44-4066-a7d4-0fef235967de	01858259070
9a0c946d-b19b-408e-af57-ac511a24d00b	84630363092
f2ee3c7a-1a55-4914-8722-9cc139ba910b	\N
5823c324-8cb0-48c4-a671-4fc92c56b0f8	\N
0eb94b0a-aa94-4e96-ada5-77b072db5f5c	\N
\.


--
-- TOC entry 5167 (class 0 OID 42178)
-- Dependencies: 233
-- Data for Name: product_category; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.product_category (id, name) FROM stdin;
1	Queijo
2	Iogurte
3	Nata
\.


--
-- TOC entry 5166 (class 0 OID 42166)
-- Dependencies: 232
-- Data for Name: product; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.product (id, name, description, category_id, measurement_unit_id, quantity, created_at, deleted_at) FROM stdin;
1	Queijo de Coalho	Queijo de coalho comum	1	1	0.800	2026-01-22 14:52:21.541287-03	\N
2	Iogurte	Iogurte no saco 900mL	2	2	0.900	2026-01-22 15:27:59.55916-03	\N
3	Queijo de manteiga	queijo de manteiga comum (1 Kg)	1	1	1.000	2026-01-23 16:24:56.859096-03	\N
6	test 2	asdfasdf	1	1	1.000	2026-01-23 21:52:33.569114-03	\N
4	Produto teste	Desc do produto teste	1	1	1.000	2026-01-23 21:46:03.214002-03	2026-01-23 21:55:00.343913-03
\.


--
-- TOC entry 5169 (class 0 OID 42186)
-- Dependencies: 235
-- Data for Name: product_price; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.product_price (id, product_id, price, price_change_date) FROM stdin;
1	1	23.50	2026-01-22 14:52:21.530136-03
2	2	3.50	2026-01-22 15:27:59.548097-03
3	3	27.00	2026-01-23 16:24:56.859096-03
4	4	0.00	2026-01-23 21:46:03.209002-03
5	6	1.00	2026-01-23 21:52:33.568114-03
\.


--
-- TOC entry 5177 (class 0 OID 42330)
-- Dependencies: 245
-- Data for Name: production; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.production (id, product_id, created_at, deleted_at, is_closed, closed_at, quantity_produced, date, gross_quantity_produced, gqp_measurement_unit_id) FROM stdin;
2	1	2026-01-24 23:21:51.118366-03	\N	f	\N	98.000	2026-01-24	98.000	1
3	1	2026-01-25 22:12:21.221985-03	\N	f	\N	100.000	2026-01-25	80.000	1
7	1	2026-01-26 15:16:47.405717-03	\N	f	\N	370.000	2026-01-26	370.000	1
8	1	2026-01-26 15:44:02.814833-03	\N	f	\N	370.000	2026-01-26	370.000	1
9	1	2026-01-26 15:44:22.98287-03	\N	f	\N	100.000	2026-01-26	100.000	1
10	1	2026-01-26 15:52:35.101831-03	\N	f	\N	370.000	2026-01-26	370.000	2
\.


--
-- TOC entry 5173 (class 0 OID 42215)
-- Dependencies: 240
-- Data for Name: raw_material; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.raw_material (id, name, description, created_at, deleted_at) FROM stdin;
1	Leite	leite coum de vaca	2026-01-21 18:27:06.195677-03	\N
2	Sal	Sal marinho comum	2026-01-21 18:27:13.741626-03	\N
3	Coalhante	Enzima coagulante	2026-01-21 18:28:05.611543-03	\N
4	Fermento lácteo	 	2026-01-21 18:28:29.747484-03	\N
5	Lenha	 	2026-01-21 18:54:07.103526-03	\N
\.


--
-- TOC entry 5172 (class 0 OID 42200)
-- Dependencies: 239
-- Data for Name: purchase; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.purchase (id, partner_id, raw_material_id, quantity, measurement_unit_id, price_per_unit, date, created_at, deleted_at, is_closed, note, closed_at) FROM stdin;
1	3aaf5d05-fbce-406b-9430-212d2848be0f	1	8000.000	2	16000.00	2026-01-21	2026-01-21 18:39:51.580228-03	\N	f		\N
2	3aaf5d05-fbce-406b-9430-212d2848be0f	1	10000.000	2	19000.00	2025-12-05	2026-01-21 18:40:43.620314-03	\N	f		\N
3	3aaf5d05-fbce-406b-9430-212d2848be0f	1	12000.000	2	23000.00	2026-01-15	2026-01-21 18:41:43.406069-03	\N	f		\N
4	f2ee3c7a-1a55-4914-8722-9cc139ba910b	1	500.000	2	1250.00	2026-01-20	2026-01-21 18:42:43.18386-03	\N	f		\N
5	f8a79ac7-da44-4066-a7d4-0fef235967de	5	8.000	3	300.00	2026-01-21	2026-01-21 18:54:31.215067-03	\N	f		\N
6	3aaf5d05-fbce-406b-9430-212d2848be0f	2	10.000	1	10.00	2026-01-05	2026-01-23 15:35:11.288542-03	\N	f		\N
7	3aaf5d05-fbce-406b-9430-212d2848be0f	4	10.000	1	57.00	2026-01-23	2026-01-23 15:37:39.729512-03	\N	f		\N
8	3aaf5d05-fbce-406b-9430-212d2848be0f	3	1.000	1	1.00	2026-01-23	2026-01-23 15:45:44.876836-03	\N	f		\N
9	3aaf5d05-fbce-406b-9430-212d2848be0f	3	1.000	1	1.00	2026-01-23	2026-01-23 15:50:23.787789-03	\N	f		\N
10	3aaf5d05-fbce-406b-9430-212d2848be0f	3	123.000	1	123.00	2026-01-23	2026-01-23 15:51:36.891174-03	\N	f		\N
11	3aaf5d05-fbce-406b-9430-212d2848be0f	1	200.000	2	400.00	2026-01-23	2026-01-23 16:09:55.662315-03	\N	f		\N
12	3aaf5d05-fbce-406b-9430-212d2848be0f	1	2000.000	2	2000.00	2026-01-23	2026-01-23 16:10:32.04336-03	\N	f		\N
13	8a881a8c-a55e-4f50-8496-300bc33259c5	1	1000.000	2	2000.00	2026-01-26	2026-01-26 14:58:00.418821-03	\N	f		\N
14	f2ee3c7a-1a55-4914-8722-9cc139ba910b	3	1.000	2	100.00	2026-01-26	2026-01-26 15:15:34.993962-03	\N	f		\N
\.


--
-- TOC entry 5178 (class 0 OID 42338)
-- Dependencies: 246
-- Data for Name: production_raw_material_purchase; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.production_raw_material_purchase (production_id, purchase_id, quantity_used) FROM stdin;
2	12	1000.000
2	5	3.000
3	12	1000.000
7	12	3500.000
7	14	0.200
8	12	3500.000
8	14	0.200
9	12	1000.000
10	12	3500.000
\.


--
-- TOC entry 5184 (class 0 OID 0)
-- Dependencies: 220
-- Name: city_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.city_id_seq', 759, true);


--
-- TOC entry 5185 (class 0 OID 0)
-- Dependencies: 227
-- Name: employee_wage_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.employee_wage_id_seq', 1, false);


--
-- TOC entry 5186 (class 0 OID 0)
-- Dependencies: 229
-- Name: measurement_unit_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.measurement_unit_id_seq', 4, true);


--
-- TOC entry 5187 (class 0 OID 0)
-- Dependencies: 231
-- Name: partner_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.partner_role_id_seq', 2, true);


--
-- TOC entry 5188 (class 0 OID 0)
-- Dependencies: 234
-- Name: product_category_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.product_category_id_seq', 3, true);


--
-- TOC entry 5189 (class 0 OID 0)
-- Dependencies: 237
-- Name: product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.product_id_seq', 6, true);


--
-- TOC entry 5190 (class 0 OID 0)
-- Dependencies: 238
-- Name: product_price_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.product_price_id_seq', 5, true);


--
-- TOC entry 5191 (class 0 OID 0)
-- Dependencies: 244
-- Name: production_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.production_id_seq', 10, true);


--
-- TOC entry 5192 (class 0 OID 0)
-- Dependencies: 241
-- Name: purchase_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.purchase_id_seq', 14, true);


--
-- TOC entry 5193 (class 0 OID 0)
-- Dependencies: 242
-- Name: raw_material_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.raw_material_id_seq', 5, true);


-- Completed on 2026-01-27 13:37:15

--
-- PostgreSQL database dump complete
--

\unrestrict CGZ4yMTqvDrbWANdeYxiKIoSNFHpxb4BbldDXyOjwBxekpzzF4aN4UP55iLXQuM

