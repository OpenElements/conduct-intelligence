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

TODO

## 📄 License

This project is licensed under the Apache License 2.0.
See the [LICENSE](LICENSE) file for details.

## 🤝 Contributing
We welcome contributions!
Please read our [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on how to get started.