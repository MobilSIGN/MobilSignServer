#MobilSign
##Elektronické podpisovanie pomocou mobilného telefónu
Cieľom projektu je vytvoriť programové vybavenie, ktoré umožní využívať mobilný telefón ako kryptografické zariadenie pre elektronický podpis. Bude umožňovať podpisovanie a šifrovanie dát, dokumentov, mailov, aplikácií atď. Predpokladá sa vývoj aplikácií pre mobilné telefóny s operačným systémom Android a iPhone OS.

###Architektúra
Architektúra projektu MobilSign je znázornená na obrázku fig. 1. Pozostáva z dvoch klientskych aplikácií a jednej aplikácie bežiacej na serveri. Klientska aplikácia v smartfóne slúži ako kryptografický token, čiže je na nej uložený súkromný kľúč používateľa. Klientska aplikácia v počítači slúži na vykonávanie všetkých náročnejších operácií ako šifrovanie, dešifrovanie, podpisovanie dokumentov atď. Komunikácia medzi klientmi prechádza šifrovane cez server, pričom odchytené dáta na serveri sa nedajú dešifrovať a tým pádom ani zneužiť.

![Architektúra projektu MobilSign](http://i60.tinypic.com/rjmutk.png "Fig. 1. Architektúra projektu MobilSign")

Fig. 1. Architektúra projektu MobilSign

###Párovanie klientov
Pred prvou komunikáciou oboch koncových zariadení, čiže smartfónu aj počítača je nutné aby sa tieto zariadenia spárovali. Párovanie v projekte MobilSign je proces, pri ktorom obe zariadenia pošlú na server určitý identifikátor, podľa ktorého server vie, že údaje prichádzajúce od jedného klienta zo spárovanej dvojice, má preposlať druhému klientovi zo spárovanej dvojice. Táto operácia je najcitlivejšia na bezpečnosť a preto je nutné aby bola maximálne možne zabezpečená a aby potenciálny útočník nemal žiadnu šancu odpočúvať komunikáciu medzi klientmi alebo dokonca podstrkovať správy v mene klientov.

Prvým krokom pri párovaní je vygenerovanie 2048 bitového kľúčového páru RSA šifrovania, ktorým sa bude šifrovať komunikácia. Táto operácia prebehne na strane počítača. Ten teda pozná verejný kľúč číslo 1 aj súkromný kľúč číslo 1 z prvého generovaného kľúčového páru. Jeden kľúč z kľúčového páru je potrebné preniesť do smartfónu. Ako najbezpečnejšiu technológiu sme vybrali prenos prostredníctvom QR kódu a fotoaparátu smartfónu. Samotný šifrovací kľúč, ktorý je potrebné preniesť do mobilného zariadenia obsahuje také veľké množstvo údajov, že po vygenerovaní QR kódu, je tento kód taký hustý, že jeho odfotenie je zdĺhavé, problematické a dokonca v niektorých prípadoch nemožné. Exponent verejného kľúča je štandardne 65 537. Modulus je teda základ celého kľúča, ktorý stačí preposielať. Do QR kódu sa pre lepšie a rýchlejšie odfotenie kóduje iba modulus verejného kľúča číslo 1. 

Po odfotení QR kódu si smartfón dokáže vyskladať modulus verejného kľúča. Aj aplikácia v počítači a aj aplikácia v smartfóne poznajú rovnaký reťazec a tým je získaný modulus kľúča. Z tohto modula sa v oboch klientskych zariadeniach vytvorí odtlačok pomocou hešovacieho algoritmu sha1 a obe zariadenie tento odtlačok pošlú na server prostredníctvom špeciálnej správy (popis správy v kapitole o komunikačnom protokole). Server tieto dve zariadenia na základe rovnakých odtlačkov spáruje. Následne sa overuje, či je spárovanie korektné a či nedošlo k jeho nabúraniu treťou stranou.

Aplikácia v smartfóne z modulusu dokáže poskladať RSA šifrovací kľúč. Tým smartfón získa verejný kľúč číslo 1. Týmto kľúčom sa bude dočasne šifrovať komunikácia. V dnešnej dobre čoraz rozvinutejších šošoviek fotoaparátov, ktoré sú čoraz menšie a ukryté v rôznych prístrojoch alebo predmetoch a dokážu ostrý obraz zachytiť z čoraz väčšej vzdialenosti je možnosť, že útočník zachytí QR kód a bude teda tiež schopný si vyskladať verejný kľúč číslo 1. V smartfóne sa preto vygeneruje kľúčový pár číslo 2 (verejný a súkromný kľúč číslo 2) a následne sa pošle cez server verejný kľúč číslo 2 zašifrovaný pomocou verejného kľúča číslo 1. Takto zašifrovaný kľúč môže počítaču podstrčiť aj útočník. Počítač rozšifruje verejný kľúč číslo 2 súkromným kľúčom číslo 1 (aj pokiaľ je podstrčený útočníkom). Tento verejný kľúč číslo 2 počítač následne zašifruje súkromným kľúčom číslo 1 a tiež verejným kľúčom číslo 2 (čiže v podstate ho zašifruje sebou samým) a pošle ho späť do smartfónu. Ten rozšifruje prijatú správu súkromným kľúčom číslo 2 a následne aj verejným kľúčom číslo 1. Rozšifrovaná prijatá správa je verejný kľúč číslo 2, ten sa overí, či je z rovnakého kľúčového páru ako súkromný kľúč číslo 2, ktorý pozná iba smartfón. Pokiaľ tieto kľúče nie sú z rovnakého kľúčového páru, nastala chyba v procese overovania párovania a pravdepodobne sa do tohto procesu zaplietla tretia osoba a párovanie zariadení sa z bezpečnostných dôvodov zruší. Pokiaľ párovanie prebehlo bez problémov, nastavia sa nové šifrovacie kľúče, ktorými sú verejný kľúč číslo 2 pre aplikáciu v počítači a súkromný kľúč číslo 2 pre aplikáciu v smartfóne a môže sa začať medzi zariadeniami zabezpečená komunikácia. Staré kľúče číslo 1 sa prestanú používať. Na obrázku fig. 2 je proces párovania znázornený graficky.

![Párovanie klientov](http://i62.tinypic.com/2uiks9k.png "Fig. 2. Párovanie klientov")

Fig. 2. Párovanie klientov

###Podpisovanie dokumentov
Všetky výpočtovo náročnejšie operácie, ktoré sa vykonávajú pri podpisovaní dokumentov sa spracúvajú na strane počítačovej aplikácie. V nej sa z dokumentu, ktorý sa používateľ snaží podpísať, vytvorí odtlačok pomocou hešovacieho algoritmu. Tento odtlačok sa pošle do mobilného zariadenia. Vďaka tomu, že sú zariadenia spárované a majú vymenené komunikačné kľúče, všetka komunikácia medzi klientmi je šifrovaná a teda aj posielaný odtlačok je šifrovaný komunikačným kľúčom počítačovej aplikácie. V smartfóne sa tento odtlačok dešifruje komunikačným kľúčom smartfónu a následne sa opäť zašifruje pomocou kľúča, ktorý pozná iba smartfón a to je jeho komunikačný a zároveň súkromný kľúč. Odtlačok sa opäť pošle cez komunikačný kanál, cez ktorý sa komunikácia šifruje komunikačnými kľúčmi, späť do aplikácie v počítači, kde sa pripojí k pôvodnému dokumentu. Týmto je dokument podpísaný. Na obrázku fig. 3 je graficky znázornené ako celé podpisovanie dokumentu v projekte MobilSign prebieha.

Pri overení podpisu sa porovnáva odtlačok pôvodnej správy s odtlačkom, ktorý vznikol dešifrovaním podpisu pomocou verejného kľúča mobilného zariadenia, ktorý je zároveň aj komunikačným kľúčom počítačovej aplikácie.

![Podpisovanie dokumentov](http://i60.tinypic.com/2ezs55x.png "Fig. 3. Podpisovanie dokumentov")

Fig. 3. Podpisovanie dokumentov

###Šifrovanie dokumentov
Proces šifrovania dokumentov je navrhnutý tak aby sa všetky výpočtovo náročné operácie vykonávali na strane počítača. Šifrovanie dokumentov sa vykonáva pomocou symetrickej šifry AES, ktorá je rádovo oveľa rýchlejšia ako asymetrické šifrovanie. Ako prvý krok sa v počítači sa vygeneruje kľúč pre AES šifru. Údaje, ktoré chce používateľ šifrovať sa zašifrujú pomocou tohto vygenerovaného kľúča AES šifrou. Kľúč, ktorý sa použil na šifrovanie údajov pomocou AES šifry sa zašifruje verejným kľúčom smartfónu, ktorý je zároveň komunikačný kľúč počítača. Tento kľúč sa pripojí k zašifrovanému súboru. Takto zašifrovaný kľúč dokáže dešifrovať iba majiteľ súkromného kľúča, ktorý pozná iba smartfón. Na obrázku fig. 4 je grafický znázornený priebeh šifrovania dokumentu v aplikácii MobilSign.

![Šifrovanie dokumentov](http://i58.tinypic.com/143josi.png "Fig. 4. Šifrovanie dokumentov")

Fig. 4. Šifrovanie dokumentov

###Dešifrovanie dokumentov
Pri dešifrovaní sa všetky výpočtovo náročné operácie vykonávajú na strane počítačového klienta a smartfón sa využíva iba ako token pre autentifikáciu používateľa a nie je zaťažený zbytočnými výpočtovými operáciami. Zašifrované údaje sa skladajú zo samotného zašifrovaného dokumentu a zašifrovaného kľúča, ktorým je zašifrovaný dokument. Aplikácia v počítači pošle prostredníctvom zabezpečeného komunikačného kanála zašifrovaný kľúč do smartfónu. Kľúč je zašifrovaný verejným kľúčom smartfónu takže ho smartfón dešifruje svojim súkromným kľúčom. Následne sa cez zabezpečený komunikačný kanál pošle späť do počítača, kde sa použije na dešifrovanie dokumentu. Na obrázku fig. 5 je graficky znázornený priebeh dešifrovanie dokumentu v aplikácii MobilSign.

![Dešifrovanie dokumentov](http://i60.tinypic.com/nn06yp.png "Fig. 5. Dešifrovanie dokumentov")

Fig. 5. Dešifrovanie dokumentov

###Komunikačný protokol
Komunikačný protokol prostredníctvom, ktorého jednotlivé aplikácie medzi sebou komunikujú je založený na socketoch. Protokol je textový a má predpísanú štruktúru. Každá správa tečúca sieťou prostredníctvom tohto komunikačného protokolu sa skladá z dvoch častí. Prvá časť označuje názov správy a jej dĺžka je presne štyri znaky. Druhá časť správy je už samotný obsah posielanej správy. Tieto dve časti sú oddelené dvojbodkou. Tento komunikačný protokol umožňuje komunikáciu medzi klientskymi aplikáciami a serverom obojsmerne. Aplikácie vedia posielať správy na server a server vie posielať prípadné odpovede alebo informačné správy späť do klientskej aplikácie. Na obrázku fig. 6 je znázornená schéma správy pre žiadosť o párovanie. Prvá časť preposielanej správy, ktorá identifikuje o aký typ správy ide nie je nijak kódovaná a je čitateľná pre človeka. Druhá časť správy je kódovaná pomocou kódovania base 64.

![Schéma správy pre žiadosť o párovanie](http://i57.tinypic.com/2w4bbs2.png "Fig. 6. Schéma správy pre žiadosť o párovanie")

Fig. 6. Schéma správy pre žiadosť o párovanie

Správy, ktoré je možné posielať prostredníctvom komunikačného protokolu:
#####RESP:[odpoveď] 
– server prostredníctvom tejto správy posiela informačné správy klientom a výsledkoch vykonaných operácii alebo aktuálnom stave pripojenia. Aktuálne existujú dve základné odpovede servera a to PAIRED, pokiaľ bolo zariadenie spárované alebo FAILED, pokiaľ zlyhala nejaká operácia.
#####PAIR:[párovací kľúč]
– správa pre žiadosť o spárovanie klienta. Párovací kľúč je SHA1 heš verejného kľúča aplikácie (popísané v kapitole 5-2). Po úspešnom spárovaní server pošle obom klientom správu RESP:paired.
#####ENCR:[údaje]
– správa pre žiadosť o zašifrovanie údajov súkromným kľúčom prijímateľa. Využíva sa pri podpisovaní dokumentov.
#####DECR:[údaje]
– správa pre žiadosť o dešifrovanie údajov súkromným kľúčom prijímateľa. Využíva sa pri dešifrovaní dokumentov.
#####MPUB:[kľúč]
– správa, ktorá sa použije počas overovania, či párovanie prebehlo korektne. Po tejto správe sa skontroluje, či posielaný kľúč je z kľúčového páru, z ktorého je súkromný kľúč smartfónu a pokiaľ je všetko v poriadku zmenia sa komunikačné kľúče.
#####SEND:[správa]
– táto správa sa využíva na testovacie účely a klienti pomocou nej si dokážu medzi sebou preposielať akékoľvek správy a tým sa vytvorí zabezpečený kanál na bežnú komunikáciu.

###Súčasný stav - mobilní klienti 
Mobilní klienti, ako Android tak aj iOS, boli vyvíjaní paralelne a teda obaja sú v rovnakom štádiu a poskytujú rovnakú funkcionalitu. 

Aplikácie dokážu odfotiť QR kód, z ktorého dokážu získať kľúč používaný na šifrovanie komunikácie a taktiež informácie potrebné na spárovanie klientov. Prostredníctvom navrhnutého protokolu a získaného komunikačného kľúča vedia aplikácie komunikovať šifrovane s PC klientom a posielať testovacie správy. 

###Súčasný stav – server a počítačový klient
Server je implementovaný v programovacom jazyku Java a umožňuje spárovanie klientov a následnú komunikáciu medzi spárovanými klientami.
Počítačový klient je hlavne pre zachovanie zachovanie podpory viacerých platforiem implementovaný v jazyku Java. Dokáže vytvárať kľúčové páry a následne modulus verejného kľúča zakódovať do QR kódu. Prostredníctvom navrhnutého protokolu a vygenerovaného komunikačného kľúča sa vie aplikácia spárovať a komunikovať šifrovane s mobilnými klientami a posielať testovacie správy.

###Smart karty
Smart karta je veľkosti kreditnej karty a je programovateľná. Po-známe niekoľko typov týchto kariet ako napríklad: Java karty, Basic karty, .NET karty, MULTOS karty. Tieto karty sú kontaktné, bezkon-taktné a hybridné. Niektoré sú chránené PIN kódom. Smart karta obsa-huje procesor, pamäť (EEPROM), RAM pamäť, náhodný generátor a kryptografický koprocesor (RSA). Výhodou týchto kariet je rýchly kryptografický koprocesor, generovanie asymetrického kľúča na karte, kvalitný a rýchly náhodný generátor a vysoká úroveň zabezpečenia. Tieto karty sa využívajú vo viacerých oblastiach: bankovníctvo, identi-fikačné karty, riadenie prístupu, atď.

###Použitá smart karta
* Gemalto TOP IM GX4 (Java card)
* Java Card 2.2.1
* Global Platform 2.1.1
* Symetrické šifry: 3DES, AES (128, 192, 256)
* Asymetrické šifry: RSA až do 2048 bitov
* Hašovacie funkcie: SHA-1
* Generovanie asymetrického kľúčového páru na karte
* Protokoly: T=0 bajtovo orientovaný
 		        T=1 blokovo orientovaný
* Baud rate až do 230 kilobitov za sekundu
* EEPROM 72 kilobajtov
* APDU buffer: 261 bajtov
* Perzistentná halda: 68 kilobajtov
* Garbage collector

###Vývoj appletov pre smart karty
Pre vývoj appletov pre smart karty je potrebných viacero nástro-jov. Medzi tieto nástroje patrí Java Development Kit 1.3 (JDK), ktorý je potrebný na kompiláciu appletu napísaného v programovacom jazyku Java a vytvorenom vo vývojovom prostredí NetBeans. Ďalší nástroj je Java Card Development Kit 2.2.1 (JCDK) a pomocou neho je súbor s koncovkou *.class skonvertovaný na súbor s koncovkou *.jar/.cap. JCDK musí byť vo verzii kompatibilnej s kartou alebo v nižšej verzii, v našom prípade je táto verzia 2.2.1. Posledný potrebný nástroj je GPShell, ktorý je potrebný na nahratie súboru na smart kartu. S apple-tom na karte sa dá komunikovať pomocou APDU (Application Protocol Data Unit) príkazov. APDU je základný komunikačný datagram, ktorý môže niesť až 260 bajtov dát. Jeho hlavička sa skladá s triedy inštrukcie (CLA), čísla inštrukcie (INS), voliteľných dát (P1, P2), dĺžky prichá-dzajúcich dát (LC), používateľských dát (DATA) a očakávanej dĺžky výstupných dát (LE). Na obrázku Fig. 7 je vidieť niekoľko typov hlavi-čiek APDU datagramu.

![Typy APDU datagramu](http://i60.tinypic.com/nla4qo.gif "Fig. 7. Typy APDU datagramu")

Fig. 7. Typy APDU datagramu

Pri práci so smart kartou je potrebné zadať kľúč, ktorý je v našom prípade pre kartu Gemalto TOP IM GX4 (47 45 4D 58 50 52 45 53 53 4F 53 41 4D 50 4C 45). Po zadaní tohto kľúča je vytvorený za-bezpečený kanál medzi klientskou aplikáciou a smart kartou a je možné začať so smart kartou pracovať.
Global platform je verejne dostupná špecifikácia pre smart karty, ktorá pokrýva životný cyklus smart karty, inštaláciu appletov, vzdia-lenú správu karty a zabezpečenú komunikáciu medzi smart kartou a užívateľskou aplikáciou.
