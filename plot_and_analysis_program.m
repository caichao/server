%% to test the java program

clc;

data = load('top100.txt');

figure;
scatter(data(:,1), data(:,2));
xlim([0, 6.8]);
ylim([0,3.2]);