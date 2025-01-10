use axum::routing::post;
use axum::{serve, Json, Router};
use dotenvy::var;
use log::__private_api::Value;
use log::info;
use std::error::Error;
use std::time::Duration;
use tower::timeout::TimeoutLayer;
use tower::ServiceBuilder;

#[tokio::main]
async fn main() -> Result<(), Box<dyn Error + Send + Sync>> {
    tracing_subscriber::fmt().init();
    dotenvy::dotenv().ok();

    info!("Starting Nabi's API on {}", var("PORT").unwrap());

    let listener = tokio::net::TcpListener::bind(var("PORT").unwrap())
        .await
        .unwrap();

    let service = ServiceBuilder::new()
        .layer(TimeoutLayer::new(Duration::from_secs(30)))
        .layer();


    Ok(())
}