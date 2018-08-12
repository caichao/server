%% to test the java program

clc;

% data = load('top100.txt');
% 
% figure;
% scatter(data(:,1), data(:,2));
% xlim([0, 6.8]);
% ylim([0,3.2]);

g1 = 0.846;
g2 = 5.892;
figure;
subplot(221);
distance = load('distance68.txt.txt')/2;
plot(distance);
subplot(222);
error = distance - g1;
plot(error);
fprintf('variance = %.2f', max(distance) - min(distance));
subplot(223);
distance = load('distance31.txt')/2;
plot(distance);
fprintf('variance = %.2f', max(distance) - min(distance));
subplot(224);
error = distance - g2;
plot(error);





