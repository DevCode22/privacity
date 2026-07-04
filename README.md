# Privacity

**Privacity** is an Android application focused on **digital security and privacy**, built to help users **identify risks, inspect suspicious links, monitor sensitive permissions, review installed apps, analyze Wi-Fi exposure, and observe network activity** from a single mobile interface.

The project was designed with a strong emphasis on **user protection**, **privacy awareness**, **preventive monitoring**, and **clear security insights**, bringing multiple privacy-oriented tools together in one modern Android experience powered by **Kotlin** and **Jetpack Compose**.

---

## Overview

**Privacity** was created to give users a clearer understanding of their Android device’s privacy and security posture. Instead of acting as a single-purpose scanner, the app centralizes different analysis and monitoring features so users can better understand:

* which apps have potentially invasive permissions;
* which links may represent phishing or suspicious behavior;
* how exposed their current Wi-Fi environment may be;
* how network usage is distributed across apps and time periods;
* which signals may indicate a less secure device environment.

The result is an application focused on **privacy, prevention, and awareness**, combining local analysis, permission monitoring, network statistics, and security indicators in a modern and accessible mobile experience.

---

## Project Goal

The goal of **Privacity** is to bring together, in a single Android application, a set of practical **security and privacy features** that are often scattered across different apps or hidden inside technical system settings.

This project was built to:

* increase user visibility over their own device;
* make sensitive permissions and potentially suspicious apps easier to inspect;
* help with the early detection of malicious or deceptive links;
* provide context around Wi-Fi exposure and network usage;
* translate technical security signals into more understandable insights;
* serve as a portfolio project focused on **modern Android development + user-centered security**.

---

## Main Features

## 1. Link Analyzer

Privacity includes a link analysis module designed to reduce user exposure to **malicious URLs, phishing attempts, suspicious redirects, and questionable domains**.

### What this module can evaluate

* phishing-related patterns;
* shortened or masked links;
* suspicious-looking domains;
* redirection structures;
* signals associated with tunnels, CDNs, or infrastructure that may hide origin or intent;
* indicators that may suggest fraud attempts, abusive data collection, or social engineering.

### User value

It provides a first layer of inspection before the user decides to trust or open a link received through messaging apps, email, social media, or other channels.

---

## 2. Suspicious App Detection

The project includes a dedicated area for inspecting installed applications, with a focus on **highlighting apps that may deserve privacy or security review**.

### What this module can highlight

* apps with excessive permissions for their expected purpose;
* requests for sensitive resources;
* combinations of permissions that may be invasive;
* applications that deserve additional user attention;
* signals that may indicate a higher privacy risk profile.

### User value

It helps users understand **which apps may pose a higher privacy risk** and supports decisions such as uninstalling an app, reviewing its permissions, or monitoring it more closely.

---

## 3. Permission Monitoring

Permission monitoring is one of the core pillars of Privacity. Its goal is to help users understand **what each installed app can access on the device**.

### What this feature provides

* permission visibility per application;
* emphasis on sensitive permissions;
* support for manual auditing of installed apps;
* clearer understanding of the access level granted to each app;
* a foundation for alerts, risk evaluation, and security scoring.

### Examples of relevant permissions

* camera;
* microphone;
* location;
* contacts;
* SMS;
* storage;
* network access;
* notifications;
* other sensitive system resources.

### User value

It gives users more control over the device’s exposure surface and encourages more conscious permission management.

---

## 4. Wi-Fi Analysis

The Wi-Fi module was designed to provide a clearer view of the user’s network environment, with a focus on **basic security, exposure, and risk context**.

### What can be observed

* open or weakly protected networks;
* nearby Wi-Fi networks detected by the device;
* public Wi-Fi usage context;
* indicators that may warn users about less trustworthy network environments;
* signals that reinforce the need for caution when using certain networks.

### User value

It helps reduce careless exposure to open or insecure Wi-Fi networks and improves awareness of network-related privacy risks.

---

## 5. Network Statistics and Usage Monitoring

Privacity also presents network-related information to help users better observe device activity.

### What can be monitored

* data consumption over time;
* general network usage overview;
* apps with higher activity;
* patterns that may justify further attention;
* additional context for privacy and security analysis.

### User value

It adds another layer of visibility into device traffic, helping users understand **which apps are communicating more frequently** and when that behavior may deserve review.

---

## 6. Password Checker

The project also includes a feature aimed at evaluating password strength.

### Possible evaluation criteria

* length;
* character variety;
* predictability;
* weak patterns;
* estimated strength level.

### User value

It promotes better security habits and helps users create or validate stronger passwords.

---

## 7. Alert Center

Privacity centralizes warnings and signals relevant to user security, turning technical events into more accessible alerts.

### Example alerts

* connection to public Wi-Fi networks;
* suspicious link detection;
* newly installed apps with sensitive permissions;
* changes in the device’s security context;
* manual review recommendations.

### User value

It brings security closer to usability by providing more direct feedback about events that may require user attention.

---

## 8. Security Score

The **Security Score** acts as a summarized view of the device’s overall security posture, aggregating risk signals and usage context.

### Factors that may influence the score

* granted sensitive permissions;
* presence of suspicious-profile apps;
* exposure to less secure Wi-Fi networks;
* risky link indicators;
* other signals used by the system to build a general security perception.

### User value

It provides a quick snapshot of the device’s current security state and works as a general attention indicator.

---

## Project Highlights

**Privacity** was designed as a project with both practical and technical value, bringing together in a single app:

* suspicious link analysis;
* app permission auditing;
* network usage monitoring;
* Wi-Fi environment observation;
* security alerts;
* aggregated risk scoring;
* a modern Jetpack Compose interface;
* a strong focus on user protection and digital privacy.

---

## Tech Stack

The project was built using modern Android technologies:

* **Kotlin**
* **Jetpack Compose**
* **Material Design 3**
* **Android SDK**
* screen- and feature-oriented project organization
* declarative UI
* focus on usability, readability, and scalability

---

## Technical Information

| Item              | Details                  |
| ----------------- | ------------------------ |
| **Project Name**  | Privacity                |
| **Platform**      | Android                  |
| **Main Language** | Kotlin                   |
| **UI Toolkit**    | Jetpack Compose          |
| **Design System** | Material Design 3        |
| **Package**       | `com.edissone.privacity` |
| **Version**       | `1.0.0`                  |
| **Target SDK**    | Android SDK 36           |
| **Minimum SDK**   | Android SDK 26           |
| **License**       | MIT                      |

---

## User Experience

The Privacity experience was designed to be **simple, visual, and informative**. The app aims to translate technical security concepts into a more accessible user experience through:

* informative cards;
* visual risk indicators;
* permission and application lists;
* network and usage statistics panels;
* alerts and recommendations;
* dedicated analysis and monitoring screens.

The main idea is that users **should not need to be cybersecurity experts** to understand when something deserves attention.

---

## Use Cases

Privacity can be useful in scenarios such as:

* quickly checking whether a received link may be dangerous;
* discovering which apps have access to sensitive device resources;
* reviewing excessive permissions granted without much attention;
* observing network consumption by installed applications;
* avoiding careless use of open Wi-Fi networks;
* getting a general view of the smartphone’s security posture;
* adopting a more conscious approach to digital privacy.

---

## Screenshots

> Add screenshots here to showcase the UI, analysis modules, dashboards, and overall app experience.

Example:

```md id="lv5cgv"
## Screenshots

| Link Analyzer |
|---|---|
| ![Links](./screenshots/linkanaliser.jpg) | ![Links](./screenshots/suspeito.jpg) |

| Apps & Permissions |
|---|---|
| ![Apps](./screenshots/Monitor.jpg) | ![Apps](./screenshots/permissoes.jpg) | ![Apps](./screenshots/perigo.jpg) |

|Score & Password|
|---|---|
| ![Apps](./screenshots/score.jpg) | ![Password](./screenshots/senha.jpg) |
```

---

## Portfolio Value

Privacity is a strong portfolio project because it demonstrates skills across multiple areas at once:

### Android Development

* building a real application with multiple screens and features;
* using **Kotlin + Jetpack Compose**;
* organizing reusable UI and modern app structure;
* focusing on usability and product design.

### Product Thinking

* clear value proposition;
* focus on solving real user problems;
* centralization of privacy and security indicators;
* balance between practical usefulness and presentation quality.

### User-Centered Security

* link analysis;
* permission monitoring;
* suspicious app inspection;
* attention to network and Wi-Fi context;
* translating technical risk into understandable user feedback.

---

## Future Improvements

Privacity was designed as a strong foundation for future expansion. Possible next steps include:

* integration with external URL reputation services;
* a more advanced risk classification engine;
* analysis history and security event tracking;
* dashboards with period-based insights;
* security report export;
* more contextual automatic recommendations;
* local persistence with Room;
* a more modular architecture;
* unit and instrumentation tests;
* multilingual support;
* accessibility and performance improvements.

---

## Installation and Run

### Requirements

* Android Studio
* a compatible JDK version
* configured Android SDK
* physical Android device or emulator

### Steps

```bash id="cix83o"
git clone https://github.com/YOUR-USERNAME/privacity.git
cd privacity
```

Then:

1. open the project in Android Studio;
2. sync Gradle dependencies;
3. run the application on an emulator or physical device.

---

## Project Status

Privacity is currently a functional and evolving project focused on building a more useful privacy and security experience for Android users.

At its current stage, the project already demonstrates a solid base in:

* modern Android UI;
* clear product direction;
* security-oriented features;
* permission, link, network, and risk-context monitoring;
* practical digital privacy thinking for Android.

---

## License

This project is licensed under the **MIT License**.

---

## Author

**DevCode**
Android developer focused on **mobile applications, privacy, digital security, and user experience**.
