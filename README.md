# ConductGuardian 🛡🤬🤖
**AI-powered Code of Conduct monitoring for GitHub comments**

**ConductGuardian** is an automated moderation assistant that helps open source maintainers identify potential violations of their project's Code of Conduct.
It uses AI (like ChatGPT) to analyze GitHub comments and notifies maintainers when inappropriate language or behavior is detected.

> [! IMPORTANT]
> This project is in the early stages of development.
> The API is not stable and may change in the future.
> Please use at your own risk.

## 🚀 Features

- 🔍 Scans all new comments from GitHub issues, PRs, and discussions
- 🤖 Uses AI to evaluate comments for CoC violations
- 🛎️ Sends real-time alerts to maintainers (e.g., via Discord)
- 🛠️ Easy to configure and extend


## ⚙️ How It Works

1. GitHub sends comment events to ConductGuardian via webhook.
2. The tool forwards the comment to a language model (e.g., ChatGPT) for evaluation.
3. If a potential violation is detected, an alert is sent to your configured notification channel.

## 🛡️ Responsible Use

ConductGuardian is a support tool, not a replacement for human judgment.
All flagged content should be reviewed by maintainers before action is taken.

## 🛠️ Installation and Usage

The app is based on Java 21, Spring Boot and can be run as a standalone application or deployed to a cloud service.
We use Maven for dependency management and building the project.
Since we run the app in Coolify, a nixpacks-based environment, we provide a `nixpacks.toml` for run the app.

### Prerequisites

In general only Java 21 is needed to build and run the app.
We highly recommend using [Eclipse Temurin](https://adoptium.net/de/) as Java distribution.
Since the project is using the Maven Wrapper, you don't need to install Maven separately.

### Build

To build the project, run the following command in the root directory:
```bash
./mvnw verify
```

This will compile the code, run (the not existing) tests, and package the application into a JAR file.

### Run

To run the application, use the following command:
```bash
./mvnw spring-boot:run
```

This will start the application on the default port (8080).
Internally the Maven plugin of Spring Boot will build the project and run the main class.

### Configuration

You can not really run the application without configuration.
The application provide several services for the following tasks:

- Receive Code of Conduct
- Check Message against Code of Conduct
- Send Notification

You will find a basic configuration in the `src/main/resources/application.properties` file.
Most of the configuration is done via environment variables.
Here the best is to create a `.env` file in the root directory and add your configuration there.
Such file is already added to the `.gitignore` file.
You can add your secrets there without worrying about them being pushed to GitHub.

## 📄 License

This project is licensed under the Apache License 2.0.
See the [LICENSE](LICENSE) file for details.

## 🤝 Contributing
We welcome contributions!
Please read our [CONTRIBUTING.md](https://github.com/OpenElements/.github/blob/main/CONTRIBUTING.md) for guidelines on how to get started.
