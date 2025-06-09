# 🧱 BetongKalkulator

**Smart kalkulator for betongsaging, kjerneborring og riving.**

En brukervennlig app som hjelper deg å beregne vekt, volum, skjæredybde og plassering av løftepunkter. Laget av fagfolk – for fagfolk.

---

## 🚀 Hovedfunksjoner

### 🔹 Startskjerm
- Tre valg: **Beregning**, **Historikk**, **Innstillinger**
- Ryddig layout med store knapper og intuitiv navigasjon

---

### 🔹 Vekt- og Volumkalkulator
Velg form og få nøyaktig vekt basert på dimensjoner, valgt enhet og betongtype:

**Støttede former:**
- Kjerne (Diameter + Tykkelse)
- Firkant (Høyde, Bredde, Tykkelse)
- Trekant (Side A, B, C + Tykkelse)
- Trapes (Side A, B, C, D + Tykkelse)

**Funksjoner:**
- Enhetsvalg: mm, cm, m, inch, foot (styres fra innstillinger)
- Automatisk omregning ved inntasting (f.eks. "1.25 m" → "1250 mm" i bakgrunnen)
- Valg av betongtype:
  - Betong, Leca, Siporex, Egendefinert
- Resultat vises i kg og tonn (dersom over 1000 kg)
- Mulighet for å kopiere eller dele resultat

---

### 🔹 Overskjæringskalkulator
Finn ut hvor dypt bladet faktisk skjærer basert på tykkelse og diameter:

- Dropdown for faste bladstørrelser (600–1600 mm)
- Manuell innlegging av betongtykkelse
- Interpolasjon mellom verdier i overskjæringstabell
- Viser:
  - Minimum og maksimum skjæredybde
  - Anbefalt minimum kjerneborringshull
  - Advarsel hvis blad ikke rekker gjennom

---

### 🔹 Løftepunktkalkulator *(Ny funksjon – 2025)*
Få forslag til hvor festepunkter bør plasseres for stabile løft:

- Støtte for Kjerne, Firkant, Trekant, Trapes
- Automatisk beregning av vekt basert på form og dimensjoner
- Velg antall festepunkter: 1, 2, 3, 4, 6
- Tekstlig forklaring av plassering (f.eks. "ca. 50 cm fra kant A og 50 cm fra kant B")
- Tar hensyn til valgt enhet og form
- Historikk og deling kommer

---

### 🔹 Historikk
- Viser siste 20 kalkulasjoner med dato og klokkeslett
- Viser form, dimensjoner, tetthet og resultat
- Funksjoner:
  - Merk én eller flere linjer
  - Slett valgte (med bekreftelse)
  - Summer valgte (viser totalvekt)
  - Del valgte (inkl. notat, form og resultat)
  - Slett alt (med bekreftelse)

---

### 🔹 Innstillinger
- **Enhetssystem:**
  - Metrisk (mm, cm, m)
  - Imperialsk (inch, foot)
- **Vektenhet:** kg eller lbs
- **Betongtype og tetthet:**
  - Standardverdier (kan tilpasses)
  - Nullstill til standard-knapp
- **Språkvalg:** Kommer
- **Utviklerinfo:** Kommer

---

## ⚙️ Teknologi

- Kotlin (Android)
- Jetpack Compose (Material 3)
- MVVM + Room + SharedPreferences
- Enhetskonvertering og inputvalidering
- Lokal lagring og enkel deling

---

## 🎯 Målgruppe

- Betongsagere
- Kjerneborrere
- Riveentreprenører
- Entreprenører og tekniske fagfolk

---

## 🛠 Planlagt videreutvikling

- Illustrasjoner for løftepunkt og former
- Vinkelfesteberegning
- PDF-eksport
- Søke-/filterfunksjon i historikk
- Reklame og kjøp for reklamefri versjon
- Offentlig lansering (Google Play)

---

## 📝 Lisens

MIT License – se `LICENSE`-filen for detaljer.

---

## 📬 Kontakt og tilbakemelding

Ønsker eller feil?  
Opprett en [issue](https://github.com/steffenhove/kalkulator/issues) – eller ta kontakt direkte.

---

