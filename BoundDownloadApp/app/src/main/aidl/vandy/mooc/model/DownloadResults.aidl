package vandy.mooc.model;

interface DownloadResults {
    oneway void sendPath(in Uri filePath);
}