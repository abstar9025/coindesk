# Coindesk 幣別＆匯率管理

## 專案概述

本專案採用Spring Boot搭配H2資料庫，整合Coindesk API以取得即時匯率，並提供CRUD功能，透過簡易前端頁面進行幣別管理。開發語言為Java 8，建置工具為Maven，ORM使用Spring Data JPA。

## 功能特色

- **首次啟動自動初始化**：啟動時自動從Coindesk下載匯率並寫入本地H2。
- **匯率 CRUD**：可新增、修改、刪除幣別，並自動更新匯率，所有操作即時反映。
- **前端互動**：使用純HTML+JavaScript，表格顯示幣別列表並格式化匯率千分位。
- **測試覆蓋**：包含資料轉換邏輯的單元測試及CRUD、整合測試。

## 快速上手

1. **Clone 專案**
   ```bash
   git clone https://github.com/abstar9025/coindesk.git
   cd coindesk
   ```
2. **編譯與啟動**
   確認已安裝JDK 8、Maven，執行：
   ```bash
   mvn clean package
   mvn spring-boot:run
   ```
   服務將在`http://localhost:8080`運行。
3. **前端介面**
   打開瀏覽器輸入：
   ```
   http://localhost:8080/index.html
   ```

## API說明

| HTTP方法 | 路徑                     | 說明                             |
| -------- | ---------------------- | -------------------------------- |
| GET      | `/api/coindesk/data`   | 取得本地匯率列表與最後更新時間       |
| GET      | `/api/currency`        | 查詢所有幣別 (Entity列表)         |
| POST     | `/api/currency`        | 新增幣別 (JSON: `code`,`name`,`rate`) |
| PUT      | `/api/currency/{code}` | 修改幣別 (JSON: `name`,`rate`)       |
| DELETE   | `/api/currency/{code}` | 刪除幣別                          |